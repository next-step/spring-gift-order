#!/bin/bash

JAR_NAME="spring-gift-0.0.1-SNAPSHOT.jar"
JAR_PATH="/home/ubuntu/$JAR_NAME"

echo ">> 배포 시작"

echo ">> 현재 실행 중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
    echo ">> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo ">> 실행중인 애플리케이션 종료 (pid: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo ">> 새 애플리케이션 배포: $JAR_PATH"
nohup java -jar -Dspring.profiles.active=prod $JAR_PATH > /dev/null 2>&1 &

echo ">> 배포 완료"