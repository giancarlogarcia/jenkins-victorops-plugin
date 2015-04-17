package org.jenkinsci.plugins.victorops;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;

public interface FineGrainedNotifier {

    @SuppressWarnings("rawtypes")
    void completed(AbstractBuild r, TaskListener listener);

}