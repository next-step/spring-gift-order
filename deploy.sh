#!/bin/bash

BUILD_PATH="/home/ubuntu/spring-gift-0.0.1-SNAPSHOT.jar"
JAR_NAME=$(basename "$BUILD_PATH")

echo "> 현재 실행 중인 애플리케이션 확인 중..."
CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 실행 중인 애플리케이션이 없습니다."
else
  echo "> 기존 애플리케이션 종료 (PID: $CURRENT_PID)"
  kill -15 "$CURRENT_PID"
  sleep 5

  # 종료가 안 됐을 때 강제 종료 시도
  if kill -0 "$CURRENT_PID" 2>/dev/null; then
    echo "> 프로세스가 아직 종료되지 않았습니다. 강제 종료합니다."
    kill -9 "$CURRENT_PID"
    sleep 2
  fi
fi

echo "> 새 애플리케이션 배포 및 실행: $JAR_NAME"
nohup java -jar "$BUILD_PATH" > /dev/null 2>&1 &

echo "> 배포 완료."
