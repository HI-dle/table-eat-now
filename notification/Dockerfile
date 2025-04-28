# 1. Build Stage
FROM gradle:8.13-jdk17 AS builder

WORKDIR /app

ARG MODULE_NAME

# -- Only copy what is necessary for dependencies first (cache optimization)
COPY settings.gradle .
COPY ${MODULE_NAME}/build.gradle ./${MODULE_NAME}/
COPY ${MODULE_NAME}/gradle/ ./${MODULE_NAME}/gradle/
COPY gradlew .

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# -- Now copy actual source code (so that dependency caching works better)
COPY common/ common/
COPY ${MODULE_NAME}/ ${MODULE_NAME}/

RUN ./gradlew :${MODULE_NAME}:clean :${MODULE_NAME}:build -x test --no-daemon

# 2. Runtime Stage
FROM amazoncorretto:17

WORKDIR /app

ARG MODULE_NAME

COPY --from=builder /app/${MODULE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
