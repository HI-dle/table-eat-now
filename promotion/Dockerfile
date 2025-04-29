FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /workspace

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

COPY common common
COPY promotion promotion

RUN chmod +x gradlew

RUN ./gradlew :promotion:bootJar -x test

FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

COPY --from=build /workspace/promotion/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]