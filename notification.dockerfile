FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY notification-service/build/libs/notification-service-0.0.1.jar /app.jar
COPY notification-service/build/resources/main/application.yaml /config/application.yaml
COPY common-module/build/resources/main/application-common.yaml /config/application-common.yaml
CMD ["java", "-jar", "/app.jar", "--spring.config.location=file:/config/"]