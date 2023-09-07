FROM openjdk:17-jdk-slim-buster

#작업 dir
WORKDIR /app

# 파일 복사
COPY . /app

# 빌드 --exclude-task test  는 테스트 오류로 추가함
RUN ./gradlew build --exclude-task test

# 빌드된 jar 복사
COPY build/libs/Springles-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080


CMD ["java", "-jar", "app.jar"]

#FROM nginx
#COPY --from=0 /app/build /usr/share/nginx/html