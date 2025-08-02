#!/bin/bash

echo "===== 배포 스크립트 시작 ====="

BUILD_PATH=$(ls /home/ubuntu/build/*.jar)
echo "빌드 파일 위치: $BUILD_PATH"

JAR_NAME=$(basename $BUILD_PATH)
echo "빌드 파일명: $JAR_NAME"

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "현재 실행 중인 프로세스가 없습니다."
  sleep 1
else
  echo "현재 실행 중인 애플리케이션 PID: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
  echo "애플리케이션이 종료되었습니다."
fi

DEPLOY_PATH=/home/ubuntu/build/
echo "배포 경로: $DEPLOY_PATH"

if [ "$BUILD_PATH" != "$DEPLOY_PATH$JAR_NAME" ]; then
  cp "$BUILD_PATH" "$DEPLOY_PATH"
fi

cd $DEPLOY_PATH

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "애플리케이션 실행: $DEPLOY_JAR"
nohup java -jar $DEPLOY_JAR --spring.config.additional-location=file:/home/ubuntu/build/secrets.properties > /dev/null 2>&1 &

echo "배포 스크립트 종료"