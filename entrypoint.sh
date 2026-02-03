#!/bin/sh
set -e

# Use Railway's PORT if provided, otherwise default to 9000
PORT=${PORT:-9000}

echo "Starting application on port $PORT with profile prod"

# Execute Java with proper JVM tuning and port configuration
exec java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:InitialRAMPercentage=50.0 \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=$PORT \
  -jar app.jar \
  --spring.profiles.active=prod
