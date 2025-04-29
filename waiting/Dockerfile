FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /workspace

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

COPY common common
COPY waiting waiting

RUN chmod +x gradlew

RUN ./gradlew :waiting:bootJar -x test

FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

COPY --from=build /workspace/waiting/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]