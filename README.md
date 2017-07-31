# VictorOps plugin for Jenkins

This is a Jenkins CI plugin that sends post-build notifications to VictorOps via the REST alerting endpoint:
http://victorops.force.com/knowledgebase/articles/Integration/Alert-Ingestion-API-Documentation

When activated, the plugin alerts VictorOps with message type "INFO" on build success and "CRITICAL" on build failure.

The project was based on the Slack plugin: https://github.com/jenkinsci/slack-plugin

# Instructions

1. Get a VictorOps account and setup an HTTP integration:
2. Grab the latest .hpi fromt he release page, or build the plugin locally (using Maven ~3.2.3 and JDK 1.8)
	mvn package
3. Place the plugin .hpi file in the {jenkins-home}/plugins/ directory
4. Restart Jenkins
5. Configure the global plugin settings: "Manage Jenkins" > "Configure System" > "VictorOps Alerting"

  - `Server url` should be simply "alert.victorops.com"
  - `API key` will be known after creating a "REST" integration. The URL
  offered by VictorOps will appear like this:

  ```
  https://alert.victorops.com/integrations/generic/20131114/alert/{API key}/$routing_key
  ```

6. Add a routing key to your Jenkins job and **don't forget to add the VictorOps post-build action**
