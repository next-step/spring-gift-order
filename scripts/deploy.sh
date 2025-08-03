#!/bin/bash

echo "> 현재 빌드된 JAR 파일 경로 찾기"
BUILD_PATH=$(ls /home/ubuntu/build/*.jar)
JAR_NAME=$(basename $BUILD_PATH)

echo "> 실행 중인 애플리케이션 종료"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 실행 중인 애플리케이션 없음"
else
  echo "> 종료 중: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"
DEPLOY_PATH=/home/ubuntu/
cp $BUILD_PATH $DEPLOY_PATH

cd $DEPLOY_PATH
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME

echo "> $DEPLOY_JAR 실행"
nohup java -jar $DEPLOY_JAR > /dev/null 2> /dev/null < /dev/null &
