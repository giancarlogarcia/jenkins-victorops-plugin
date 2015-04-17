package org.jenkinsci.plugins.victorops;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

public class VictorOpsNotifier extends Notifier {

    private static final Logger logger = Logger.getLogger(VictorOpsNotifier.class.getName());

    private String serverUrl;
    private String apiKey;
    private String routingKey;

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    @DataBoundConstructor
    public VictorOpsNotifier(final String serverUrl, final String apiKey, final String routingKey) {
        super();
        this.serverUrl = serverUrl;
        this.serverUrl = apiKey;
        this.routingKey = routingKey;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public VictorOpsService newVictorOpsService(String serverUrl, String apiKey, String routingKey) {
        // Settings are passed here from the job, if they are null, use global settings
        if (serverUrl == null) {
            serverUrl = getServerUrl();
        }
        if (apiKey == null) {
            apiKey = getApiKey();
        }
        if (routingKey == null) {
            routingKey = getRoutingKey();
        }

        return new StandardVictorOpsService(serverUrl, apiKey, routingKey);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    public void update() {
        this.serverUrl = getDescriptor().serverUrl;
        this.apiKey = getDescriptor().apiKey;
        this.routingKey = getDescriptor().routingKey;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String serverUrl;
        private String apiKey;
        private String routingKey;

        public DescriptorImpl() {
            load();
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException {
            serverUrl = sr.getParameter("victoropsServerUrl");
            apiKey = sr.getParameter("victoropsApiKey");
            routingKey = sr.getParameter("victoropsRoutingKey");
            if (serverUrl != null && !serverUrl.endsWith("/")) {
                serverUrl = serverUrl + "/";
            }
            save();
            return super.configure(sr, formData);
        }

        @Override
        public String getDisplayName() {
            return "VictorOps Alerting";
        }

    }

    public static class VictorOpsJobProperty extends hudson.model.JobProperty<AbstractProject<?, ?>> {

        private String serverUrl;
        private String apiKey;
        private String routingKey;
        private boolean notifySuccess;
        private boolean notifyFailure;

        @DataBoundConstructor
        public VictorOpsJobProperty(String serverUrl,
                                    String apiKey,
                                    String routingKey,
                                    boolean notifyFailure,
                                    boolean notifySuccess) {
            this.serverUrl = serverUrl;
            this.apiKey = apiKey;
            this.routingKey = routingKey;
            this.notifyFailure = notifyFailure;
            this.notifySuccess = notifySuccess;
        }

        @Exported
        public String getServerUrl() {
            return serverUrl;
        }

        @Exported
        public String getApiKey() {
            return apiKey;
        }

        @Exported
        public String getRoutingKey() {
            return routingKey;
        }

        @Exported
        public boolean getNotifySuccess() {
            return notifySuccess;
        }

        @Override
        public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
            Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
            for (Publisher publisher : map.values()) {
                if (publisher instanceof VictorOpsNotifier) {
                    ((VictorOpsNotifier) publisher).update();
                }
            }
            return super.prebuild(build, listener);
        }

        @Exported
        public boolean getNotifyFailure() {
            return notifyFailure;
        }

        @Extension
        public static final class DescriptorImpl extends JobPropertyDescriptor {

            public String getDisplayName() {
                return "VictorOps Notifications";
            }

            @Override
            public boolean isApplicable(Class<? extends Job> jobType) {
                return true;
            }

            @Override
            public VictorOpsJobProperty newInstance(StaplerRequest sr, JSONObject formData) throws hudson.model.Descriptor.FormException {
                return new VictorOpsJobProperty(
                    sr.getParameter("victoropsServerUrl"),
                    sr.getParameter("victoropsApiKey"),
                    sr.getParameter("victoropsRoutingKey"),
                    sr.getParameter("victoropsNotifyFailure") != null,
                    sr.getParameter("victoropsNotifySuccess") != null);
            }
        }
    }

}
