FROM adoptopenjdk/openjdk11-openj9:jre-11.0.3_7_openj9-0.14.0-alpine
# -- Files --
ADD gitignore/wildfly/bootablejar/cloture-bootable.jar /opt/cloture-bootable.jar
ADD target/cloture.war /opt/cloture.war

EXPOSE 8080

# -- SINGLE SIGN ON SYSTEM : KEYCLOAK --
ENV keycloak.auth-server-url https://security.dgbf.ci:8443/auth
ENV KEYCLOAK_REALM SIIBTEST
ENV keycloak.resource mic-cloture
ENV keycloak.secret=a827b434-af83-42f5-b6af-92f0be4983a1
ENV cloture.server.client.rest.uri=http://mic-cloture-api/api

# -- USER INTERFACE --
ENV SIIBC_NAME SIGOBE
# ENV cyk.variable.system.web.home.url http://siib.dgbf.ci
ENV cyk.variable.system.web.context /cloture

ENTRYPOINT ["java", "-jar","-Djava.net.preferIPv4Stack=true","-Djava.net.preferIPv4Addresses=true","/opt/cloture-bootable.jar","--deployment=/opt/cloture.war"]