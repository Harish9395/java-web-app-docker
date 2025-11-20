FROM tomcat:8.5-jdk8
COPY target/java-web-app*.war /usr/local/tomcat/webapps/java-web-app.war
# Set environment variable for AppConfig sidecar URL
ENV CONFIG_URL=http://localhost:2772/applications/my-app/environments/dev/configurations/runtime-config
# Copy the environment loader script
COPY load-env.sh /usr/local/bin/load-env.sh
RUN chmod +x /usr/local/bin/load-env.sh
# Make load-env.sh the entrypoint
ENTRYPOINT ["/usr/local/bin/load-env.sh"]
