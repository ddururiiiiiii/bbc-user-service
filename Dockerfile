# 1. Java 17이 설치된 베이스 이미지 사용
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성 및 이동
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너에 복사
COPY build/libs/bbc-user-service-0.0.1-SNAPSHOT.jar app.jar

# 4. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]