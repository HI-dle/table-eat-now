FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /workspace

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

COPY notification/.env.notification .env.notification
COPY common common
COPY notification notification

RUN chmod +x gradlew

RUN ./gradlew :notification:bootJar -x test

FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

COPY --from=build /workspace/notification/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]