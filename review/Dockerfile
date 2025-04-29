FROM eclipse-temurin:17-jdk as build

WORKDIR /workspace

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

COPY common common
COPY review review

RUN chmod +x gradlew

RUN ./gradlew :review:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/review/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y curl

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
