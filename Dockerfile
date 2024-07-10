# jdk17 Image Start
FROM openjdk:17-jdk-alpine

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
COPY ${JAR_FILE} app.jar

# Firebase 서비스 계정 파일 복사
COPY src/main/resources/iplan-firebase.json /app/iplan-firebase.json

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
