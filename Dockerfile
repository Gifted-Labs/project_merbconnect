# =============================================================================
# MerbsConnect Backend - Production Dockerfile (Railway Compatible)
# =============================================================================

# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# =============================================================================
# Production Stage
# =============================================================================
FROM eclipse-temurin:21-jre-alpine

# Add labels for container metadata
LABEL maintainer="MerbsConnect <admin@merbsconnect.com>"
LABEL version="1.0"
LABEL description="MerbsConnect Event Management API"

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 -S merbsconnect && \
    adduser -u 1001 -S merbsconnect -G merbsconnect

WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && chown -R merbsconnect:merbsconnect /app

# Copy JAR from builder
COPY --from=builder --chown=merbsconnect:merbsconnect /app/target/merbsconnect-0.0.1-SNAPSHOT.jar app.jar

# Switch to non-root user
USER merbsconnect

# Railway uses PORT env variable (defaults to 9000 if not set)
ENV PORT=9000
EXPOSE $PORT

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# Run with production profile, using Railway's PORT
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar --spring.profiles.active=prod"]

