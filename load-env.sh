#!/bin/bash
set -e

CONFIG_FILE=/app/config/app.env
mkdir -p /app/config

update_env_file() {
    echo "Fetching latest config from AppConfig..."
    CONFIG_JSON=$(curl -s "$CONFIG_URL")

    # Parse JSON
    USERNAME=$(echo "$CONFIG_JSON" | jq -r '.username')
    LOG_LEVEL=$(echo "$CONFIG_JSON" | jq -r '.logLevel')
    FEATURE_FLAG=$(echo "$CONFIG_JSON" | jq -r '.featureFlag')

    # Write env file
    cat > $CONFIG_FILE <<EOF
USERNAME=$USERNAME
LOG_LEVEL=$LOG_LEVEL
FEATURE_FLAG=$FEATURE_FLAG
EOF

    # Export variables
    export USERNAME LOG_LEVEL FEATURE_FLAG
    echo "Updated app.env:"
    cat $CONFIG_FILE
}

# Initial load
update_env_file

# Optional: auto-refresh every 15 seconds
while true; do
    sleep 15
    update_env_file
done &

# Start Tomcat
exec catalina.sh run
