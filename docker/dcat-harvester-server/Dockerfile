FROM tomcat:8-jre8

ADD build/dcat-admin-webapp-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/dcat-admin-webapp.war
ADD build/dcat-api-app-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/dcat-api-app.war
ADD build/dcat-harvester-app-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/dcat-harvester-app.war

ADD tomcat-users.xml /usr/local/tomcat/conf/
ADD server.xml /usr/local/tomcat/conf/


WORKDIR /usr/local/tomcat/
