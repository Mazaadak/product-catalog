FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/product_catalog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 18089
ENTRYPOINT ["java", "-jar", "app.jar"]
