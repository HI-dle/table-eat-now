FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /workspace

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

COPY common common
COPY server server

RUN chmod +x gradlew

RUN ./gradlew :server:bootJar -x test

FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

COPY --from=build /workspace/server/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]