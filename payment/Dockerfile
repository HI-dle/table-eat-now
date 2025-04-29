FROM eclipse-temurin:17-jdk as build

WORKDIR /workspace

COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

COPY common common
COPY payment payment

RUN chmod +x gradlew

RUN ./gradlew :payment:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/payment/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y curl

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
