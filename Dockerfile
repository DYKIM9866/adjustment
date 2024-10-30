# 빌드 이미지로 OpenJDK 21 & gradle 지정
FROM gradle:8.10.2-jdk-21-and-22-alpine AS build

# 소스코드 복사할 디렉토리 생성 후 app폴더 들어감
WORKDIR /app

# gradlew를 /app 폴더에 복사
COPY gradlew .
# gradle 폴더를 /app에 복사
COPY gradle gradle
# gradlew 권한 부여
RUN chmod +x ./gradlew

# build.gradle파일 /app 폴더에 복사
COPY build.gradle .
# settings.gradle파일 /app 폴더에 복사
COPY settings.gradle .
# 의존성 다운로드
RUN ./gradlew --no-daemon dependencies

# 소스코드 복사 및 애플리케이션 빌드
COPY . .
RUN ./gradlew --no-daemon clean build

# 실행 스테이지
FROM openjdk:21-jdk-slim
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java"]
CMD ["-jar","/app/app.jar"]