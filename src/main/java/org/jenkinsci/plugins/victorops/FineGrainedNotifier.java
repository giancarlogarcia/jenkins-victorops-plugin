package org.jenkinsci.plugins.victorops;

import hudson.model.AbstractBuild;

public interface FineGrainedNotifier {

    @SuppressWarnings("rawtypes")
    void completed(AbstractBuild r);

}