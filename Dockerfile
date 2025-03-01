FROM openjdk:21-jdk-slim

WORKDIR /app

COPY localhost.p12 /app/
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]