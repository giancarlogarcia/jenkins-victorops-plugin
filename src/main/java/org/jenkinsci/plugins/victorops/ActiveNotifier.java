package org.jenkinsci.plugins.victorops;

import hudson.Util;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;

import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public class ActiveNotifier implements FineGrainedNotifier {

    private static final Logger logger = Logger.getLogger(VictorOpsListener.class.getName());

    VictorOpsNotifier notifier;

    public ActiveNotifier(VictorOpsNotifier notifier) {
        super();
        this.notifier = notifier;
    }

    private VictorOpsService getVictorOps(AbstractBuild r) {
        AbstractProject<?, ?> project = r.getProject();
        String serverUrl = Util.fixEmpty(project.getProperty(VictorOpsNotifier.VictorOpsJobProperty.class).getServerUrl());
        String apiKey = Util.fixEmpty(project.getProperty(VictorOpsNotifier.VictorOpsJobProperty.class).getApiKey());
        String routingKey = Util.fixEmpty(project.getProperty(VictorOpsNotifier.VictorOpsJobProperty.class).getRoutingKey());
        return notifier.newVictorOpsService(serverUrl, apiKey, routingKey);
    }

    public void completed(AbstractBuild r, TaskListener listener) {
        AbstractProject<?, ?> project = r.getProject();
        VictorOpsNotifier.VictorOpsJobProperty jobProperty = project.getProperty(VictorOpsNotifier.VictorOpsJobProperty.class);
        if (jobProperty == null) {
            logger.warning("Project " + project.getName() + " has no VictorOps configuration.");
            return;
        }
        Result result = r.getResult();
        AbstractBuild<?, ?> previousBuild = project.getLastBuild();
        do {
            previousBuild = previousBuild.getPreviousCompletedBuild();
        } while (previousBuild != null && previousBuild.getResult() == Result.ABORTED);
        if ((result == Result.FAILURE && jobProperty.getNotifyFailure())
            || (result == Result.SUCCESS && jobProperty.getNotifySuccess())) {
            String status = getBuildStatus(r);
            String incident = getBuildIncident(r);
            listener.getLogger().println("Posting status '" + status + "' to VictorOps incident '" + incident + "'.");
            getVictorOps(r).publish(status, getBuildMessage(r), incident);
        }
    }

    static String getBuildStatus(AbstractBuild r) {
        Result result = r.getResult();
        if (result == Result.SUCCESS) {
            return "INFO";
        } else if (result == Result.FAILURE) {
            return "CRITICAL";
        } else {
            return "WARNING";
        }
    }

    static String getBuildMessage(AbstractBuild r) {
        AbstractProject<?, ?> project = r.getProject();
        return project.getName() + " completed with status '" + r.getResult().toString() + "'";
    }

    static String getBuildIncident(AbstractBuild r) {
        AbstractProject<?, ?> project = r.getProject();
        return "Jenkins " + project.getLastBuild().toString();
    }

}