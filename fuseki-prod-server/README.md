# War file

Download zip file from: http://apache.uib.no/jena/binaries/apache-jena-fuseki-2.3.1.zip

Unzip and copy ```fuseki.war``` into ```/usr/local/tomcat/webapps/```

# Fuseki

Create the following directories

```bash
mkdir /etc/fuseki/
mkdir /etc/fuseki/databases/
mkdir /etc/fuseki/databases/dcat/
mkdir /etc/fuseki/databases/admin/
mkdir /etc/fuseki/configuration/

```

# Add the config files for the two endpoints (admin and dcat)

admin-config.ttl -> /etc/fuseki/configuration/
dcat-config.ttl -> /etc/fuseki/configuration/

# Add the security config

shiro.ini -> /etc/fuseki/

# Final

Restart tomcat.

