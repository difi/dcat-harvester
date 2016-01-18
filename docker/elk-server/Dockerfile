FROM ubuntu:latest
ENV TERM xterm

###############################################################################
#                                INSTALLATION
###############################################################################

### install Elasticsearch

RUN apt-get update -qq \
 && apt-get install -qqy curl

RUN curl http://packages.elasticsearch.org/GPG-KEY-elasticsearch | apt-key add -
RUN echo deb http://packages.elasticsearch.org/elasticsearch/2.x/debian stable main > /etc/apt/sources.list.d/elasticsearch-2.x.list

RUN apt-get update -qq \
 && apt-get install -qqy \
		elasticsearch \
		openjdk-7-jdk \
    vim
    #npm

### install elasticsearch plugins

RUN /usr/share/elasticsearch/bin/plugin install mobz/elasticsearch-head
## RUN ln -s /usr/bin/nodejs /usr/bin/node
#RUN npm install elasticdump –g

### install Logstash

ENV LOGSTASH_HOME /opt/logstash
ENV LOGSTASH_PACKAGE logstash-2.1.1.tar.gz

RUN mkdir ${LOGSTASH_HOME} \
 && curl -O https://download.elasticsearch.org/logstash/logstash/${LOGSTASH_PACKAGE} \
 && tar xzf ${LOGSTASH_PACKAGE} -C ${LOGSTASH_HOME} --strip-components=1 \
 && rm -f ${LOGSTASH_PACKAGE} \
 && groupadd -r logstash \
 && useradd -r -s /usr/sbin/nologin -d ${LOGSTASH_HOME} -c "Logstash service user" -g logstash logstash \
 && mkdir -p /var/log/logstash /etc/logstash/conf.d \
 && chown -R logstash:logstash ${LOGSTASH_HOME} /var/log/logstash

ADD build/logstash-init /etc/init.d/logstash
RUN sed -i -e 's#^LS_HOME=$#LS_HOME='$LOGSTASH_HOME'#' /etc/init.d/logstash \
 && chmod +x /etc/init.d/logstash

 ### install Kibana

 ENV KIBANA_HOME /opt/kibana
 ENV KIBANA_PACKAGE kibana-4.3.1-linux-x64.tar.gz

 RUN mkdir ${KIBANA_HOME} \
  && curl -O https://download.elasticsearch.org/kibana/kibana/${KIBANA_PACKAGE} \
  && tar xzf ${KIBANA_PACKAGE} -C ${KIBANA_HOME} --strip-components=1 \
  && rm -f ${KIBANA_PACKAGE} \
  && groupadd -r kibana \
  && useradd -r -s /usr/sbin/nologin -d ${KIBANA_HOME} -c "Kibana service user" -g kibana kibana \
  && chown -R kibana:kibana ${KIBANA_HOME}

 ADD build/kibana-init /etc/init.d/kibana
 RUN sed -i -e 's#^KIBANA_HOME=$#KIBANA_HOME='$KIBANA_HOME'#' /etc/init.d/kibana \
  && chmod +x /etc/init.d/kibana

RUN /opt/kibana/bin/kibana plugin --install elastic/sense

###############################################################################
#                               CONFIGURATION
###############################################################################

### configure Elasticsearch
ADD build/elasticsearch.yml /etc/elasticsearch/elasticsearch.yml

### configure kibana
ADD build/kibana.yml /opt/kibana/config/kibana.yml
ADD build/kibana-exported.json /opt/kibana/config/kibana-exported.json

### configure logstash
ADD build/logstash.conf /etc/logstash/conf.d/logstash.conf
ADD build/difi_template.json /etc/logstash/conf.d/difi_template.json

###############################################################################
#                                   START
###############################################################################

ADD build/start.sh /usr/local/bin/start.sh
RUN chmod +x /usr/local/bin/start.sh
#RUN elasticdump --input=/opt/kibana/config/kibana-exported.json --output=http://localhost:9200/.kibana --type=data

EXPOSE 5601 9200 9300 5000 5044
VOLUME /var/lib/elasticsearch

CMD [ "/usr/local/bin/start.sh" ]
