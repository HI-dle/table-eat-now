# 1단계: 빌드 스테이지
FROM eclipse-temurin:17-jdk-alpine as build

# 루트 기준
WORKDIR /workspace

# 루트에 필요한 파일 복사
COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle

# 서브 모듈 복사
COPY common common
COPY reservation reservation

# Gradle 실행 권한 부여
RUN chmod +x gradlew

# reservation 모듈만 bootJar 빌드 (테스트 제외)
RUN ./gradlew :reservation:bootJar -x test

# 2단계: 런타임 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 결과물 가져오기
COPY --from=build /workspace/reservation/build/libs/*.jar app.jar

# curl 설치 (옵션)
RUN apt-get update && apt-get install -y curl

## 8083 포트 오픈 (reservation)
EXPOSE 8083

# 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]