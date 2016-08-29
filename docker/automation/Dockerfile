FROM ubuntu

ENV TERM xterm

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get update && apt-get install -y curl

ADD ./upload_to_kibana.sh /tmp/upload_to_kibana.sh
ADD ./dashboard.json /tmp/dashboard.json

RUN chmod +x /tmp/upload_to_kibana.sh

CMD /tmp/upload_to_kibana.sh
