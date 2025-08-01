FROM eclipse-temurin:21-jdk-jammy

COPY . app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]