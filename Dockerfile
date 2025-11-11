FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /task

RUN ["apk", "add", "nano"]

COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
