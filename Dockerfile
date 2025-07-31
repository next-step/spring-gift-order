FROM openjdk:21-jdk-slim

CMD ["./gradlew", "clean", "build"]

VOLUME /backend

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]


