FROM tomcat:8.5-jdk8
ENV MY_ENV_VAR="hello-from-docker"
COPY target/java-web-app*.war /usr/local/tomcat/webapps/java-web-app.war
