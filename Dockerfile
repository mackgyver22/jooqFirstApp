# Simple runtime image - JAR must be built before running docker-compose
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the pre-built jar
COPY target/*.jar app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
