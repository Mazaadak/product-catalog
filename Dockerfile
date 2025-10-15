# Stage 1: Build the application using a Maven image
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies first (this layer is cached)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping tests for a faster build
RUN mvn package -DskipTests

# Stage 2: Create the final, lightweight image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/product_catalog-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 18089

# The command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]