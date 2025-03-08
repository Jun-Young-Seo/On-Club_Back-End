FROM amazoncorretto:17

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 타임존 설정 (한국 시간: Asia/Seoul)
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo "Asia/Seoul" > /etc/timezone

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
