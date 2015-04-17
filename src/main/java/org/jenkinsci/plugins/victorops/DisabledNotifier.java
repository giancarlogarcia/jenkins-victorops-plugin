package org.jenkinsci.plugins.victorops;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;

@SuppressWarnings("rawtypes")
public class DisabledNotifier implements FineGrainedNotifier {

    public void completed(AbstractBuild r, TaskListener listener) {
    }

}