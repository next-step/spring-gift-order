#!/bin/bash

echo "[1] 최신 .jar 파일을 찾는 중"
BUILD_PATH=$(ls ./build/libs/*.jar | tail -n 1)
JAR_NAME=$(basename $BUILD_PATH)

echo "[2] 최신 JAR 파일 이름: $JAR_NAME"

echo "[3] 실행 중인 프로세스 확인 중"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
  echo "현재 실행 중인 애플리케이션이 없습니다."
else
  echo "기존 애플리케이션 종료: PID $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "[4] 새 버전 실행 중"
nohup java -jar $BUILD_PATH > ./app.log 2>&1 &

echo "[5] 배포 완료. 로그 파일: ./app.log"
