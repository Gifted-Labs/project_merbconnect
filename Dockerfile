# Build Stage
FROM ubuntu:latest as builder

# Install OpenJDK 21 and Maven
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build the application skipping tests
RUN mvn clean package -Dmaven.test.skip=true

# Run Stage
FROM ubuntu:latest

# Install OpenJDK 21 JRE
RUN apt-get update && \
    apt-get install -y openjdk-21-jre-headless && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the specific JAR file
COPY --from=builder /app/target/merbsconnect-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]