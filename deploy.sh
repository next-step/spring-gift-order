#!/bin/bash

echo "=== 배포 시작 ==="

BUILD_PATH=$(ls /home/ubuntu/*.jar)
JAR_NAME=$(basename $BUILD_PATH)
DEPLOY_JAR="/home/ubuntu/$JAR_NAME"

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
  echo "> 실행 중인 프로세스 없음"
else
  echo "> 기존 프로세스 종료: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 애플리케이션 실행: $DEPLOY_JAR"
nohup java -jar "$DEPLOY_JAR" > app.log 2>&1 &

echo "=== 배포 완료 ==="