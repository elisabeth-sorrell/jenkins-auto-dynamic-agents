FROM jenkins/jenkins:lts

USER root
WORKDIR /tmp
COPY exec_agent.sh /var/jenkins_home/exec_agent.sh
COPY consul-config.hcl /var/jenkins_home/consul-config.hcl
COPY agent_defs.config.tpl /var/jenkins_home/agent_defs.config.tpl
COPY exec_agent_wrapper.sh /var/jenkins_home/exec_agent_wrapper.sh
COPY launchAgent.groovy /var/jenkins_home/launchAgent.groovy
RUN chmod 755 /var/jenkins_home/exec_agent.sh /var/jenkins_home/agent_defs.config.tpl; \
    chown jenkins:jenkins /var/jenkins_home/exec_agent.sh /var/jenkins_home/agent_defs.config.tpl
RUN curl -O https://releases.hashicorp.com/consul-template/0.21.3/consul-template_0.21.3_linux_amd64.tgz
RUN tar -zxf consul-template_0.21.3_linux_amd64.tgz; \
    ls -al; \
    mv consul-template /usr/local/bin;

