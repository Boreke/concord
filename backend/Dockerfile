FROM eclipse-temurin:17-jdk-alpine

RUN mkdir -p /app
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]