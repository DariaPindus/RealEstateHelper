FROM jboss/wildfly
ARG WAR_FILE=target/*.war
ADD ${WAR_FILE} /opt/jboss/wildfly/standalone/deployments/
CMD ["sh", "-c", "sleep 20 && /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0"]