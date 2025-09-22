FROM amazoncorretto:17-alpine
# 시간대 설정
RUN apk update && apk add --no-cache tzdata
ENV TZ=Asia/Seoul
RUN cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

LABEL authors="ambition-kwon"

WORKDIR /app

COPY build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
