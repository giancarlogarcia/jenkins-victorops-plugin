package org.jenkinsci.plugins.victorops;

public interface VictorOpsService {

    boolean publish(String status, String message);

    boolean publish(String status, String message, String incident);

}