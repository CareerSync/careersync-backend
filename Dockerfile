FROM openjdk:11-jdk-slim-buster
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "/app.jar"]