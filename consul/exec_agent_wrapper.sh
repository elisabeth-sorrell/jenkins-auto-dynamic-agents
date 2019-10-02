#!/usr/bin/env bash
trap "kill -TERM $child" SIGTERM

/var/jenkins_home/exec_agent.sh
child=$!
echo "child is $child"
wait "$child"
