FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY discovery-server/build/libs/discovery-server-0.0.1.jar /app.jar
COPY discovery-server/build/resources/main/application.yaml /config/application.yaml
COPY common-module/build/resources/main/application-common.yaml /config/application-common.yaml
CMD ["java", "-jar", "/app.jar", "--spring.config.location=file:/config/"]