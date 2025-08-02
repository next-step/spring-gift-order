#!/bin/bash

echo ">>> 배포 시작"

BUILD_PATH=$(ls ./build/libs/*.jar)
JAR_NAME=$(basename "$BUILD_PATH")

echo ">>> 현재 실행 중인 앱 종료"
CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]; then
  echo ">>> 실행 중인 앱 없음"
else
  echo ">>> 실행 중인 앱 종료: PID $CURRENT_PID"
  kill -15 "$CURRENT_PID"
  sleep 5
fi

echo ">>> JAR 실행 시작"
DEPLOY_JAR=$BUILD_PATH
nohup java -jar "$DEPLOY_JAR" > /dev/null 2>&1 &

echo ">>> 배포 완료"