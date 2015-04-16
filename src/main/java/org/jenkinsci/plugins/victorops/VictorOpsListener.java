package org.jenkinsci.plugins.victorops;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;

import java.util.Map;
import java.util.logging.Logger;

@Extension
@SuppressWarnings("rawtypes")
public class VictorOpsListener extends RunListener<AbstractBuild> {

    private static final Logger logger = Logger.getLogger(VictorOpsListener.class.getName());

    public VictorOpsListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onCompleted(AbstractBuild r, TaskListener listener) {
        getNotifier(r.getProject()).completed(r);
        super.onCompleted(r, listener);
    }

    @SuppressWarnings("unchecked")
    FineGrainedNotifier getNotifier(AbstractProject project) {
        Map<Descriptor<Publisher>, Publisher> map = project.getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof VictorOpsNotifier) {
                ((VictorOpsNotifier)publisher).update();
                return new ActiveNotifier((VictorOpsNotifier) publisher);
            }
        }
        return new DisabledNotifier();
    }

}
