FROM tomcat:8.5-jdk8
# Create directory for dynamic config
RUN mkdir -p /app/config
# Install jq for parsing JSON
RUN apt-get update && apt-get install -y jq curl
COPY target/java-web-app*.war /usr/local/tomcat/webapps/java-web-app.war
# Set environment variable for AppConfig sidecar URL
ENV CONFIG_URL=http://localhost:2772/applications/my-app/environments/dev/configurations/runtime-config
# Copy the environment loader script
COPY load-env.sh /usr/local/bin/load-env.sh
RUN chmod +x /usr/local/bin/load-env.sh
# Make load-env.sh the entrypoint
CMD ["/bin/bash", "-c", "/usr/local/bin/load-env.sh & catalina.sh run"]
