# VictorOps plugin for Jenkins

This is a Jenkins CI plugin that sends post-build notifications to VictorOps via the REST alerting endpoint:
http://victorops.force.com/knowledgebase/articles/Integration/Alert-Ingestion-API-Documentation

When activated, the plugin alerts VictorOps with message type "INFO" on build success and "CRITICAL" on build failure.

The project was based on the Slack plugin: https://github.com/jenkinsci/slack-plugin

# Instructions

Install Maven (I used 3.2.3) and JDK (I used 1.8).

Right now this project isn't hosted by jenkisci, so there are a few extra steps you'll need to follow.

1. Get a VictorOps account and setup an HTTP integration:
2. Build the plugin locally
	mvn package
3. Place the plugin .hpi file in the {jenkins-home}/plugins/ directory
4. Restart Jenkins
5. Configure the global plugin settings: "Manage Jenkins" > "Configure System" > "VictorOps Alerting"
6. Add a routing key to your Jenkins job and **don't forget to add the VictorOps post-build action**
