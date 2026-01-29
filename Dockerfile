# Use an official OpenJDK 21 runtime as the base image for the build stage
FROM eclipse-temurin:21-jdk-jammy as builder

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build files
COPY pom.xml .
COPY src ./src

# Build the application using Maven (fail fast if build fails)
RUN mvn clean package -DskipTests && \
    ls -la /app/target/

# Use a smaller base image for the final stage
FROM eclipse-temurin:21-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder stage (be more specific)
COPY --from=builder /app/target/*.jar app.jar

# Verify the JAR exists
RUN ls -la /app/

# Expose the port your application will run on
EXPOSE 9000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]