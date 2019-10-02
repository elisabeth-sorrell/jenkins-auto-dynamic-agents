FROM jenkins/slave

USER root
WORKDIR /tmp

RUN curl -O https://releases.hashicorp.com/consul/1.6.0/consul_1.6.0_linux_amd64.zip
RUN unzip consul_1.6.0_linux_amd64.zip; \
    ls -al; \
    mv consul /usr/local/bin;
RUN mkdir /tmp/consul.d; mkdir /tmp/consul
COPY web.json /tmp/consul.d/web.json

