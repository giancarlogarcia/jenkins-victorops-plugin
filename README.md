# VictorOps plugin for Jenkins

This project is based on the Slack plugin implementation found here:

https://github.com/jenkinsci/slack-plugin

# Jenkins instructions

Right now this project isn't hosted by jenkisci, so there are a few extra steps you'll need to follow.

1. Get a VictorOps account and setup an HTTP integration:
	http://victorops.force.com/knowledgebase/articles/Integration/Alert-Ingestion-API-Documentation
2. Build the plugin locally
	mvn package
3. Place the plugin .hpi file in the {jenkins-home}/plugins/ directory
4. Restart Jenkins
5. Configure the global plugin settings: "Manage Jenkins" > "Configure System" > "VictorOps Alerting"
6. Add a routing key to your Jenkins job and **don't forget to add the VictorOps post-build action**

# Developer instructions

Install Maven (I used 3.2.3) and JDK (I used 1.8).
