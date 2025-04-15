# Use a lightweight JDK image
FROM openjdk:17-jdk-slim

# Set app directory
WORKDIR /app

# Copy build files
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
