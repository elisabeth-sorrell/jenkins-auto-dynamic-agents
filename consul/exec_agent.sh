#!/bin/bash

java -jar "/usr/local/bin/jenkins-cli.jar" -s "http://localhost:8080" -noKeyAuth -auth "@/var/jenkins_home/.jenkins-cli-auth" groovy = < /var/jenkins_home/launchAgent.groovy

while true;
do 
 sleep 5s
done
