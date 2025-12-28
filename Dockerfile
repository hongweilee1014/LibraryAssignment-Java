# --- Stage 1: Build the Application ---
FROM maven:3.9.12-openjdk-17 AS build
WORKDIR /app

# Copy the project files
COPY . .

# Build the app (skipping tests to speed up deployment)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (Standard for Spring Boot)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]