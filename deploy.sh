#!/bin/bash

BUILD_PATH=$(ls /home/ubuntu/spring-gift.jar)
JAR_NAME=$(basename $BUILD_PATH)

echo "> 현재 실행중인 애플리케이션 종료..."
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]; then
  echo "> 실행 중인 앱 없음"
else
  echo "> $CURRENT_PID 종료"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 실행..."
DEPLOY_PATH=/home/ubuntu/
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME

nohup java -jar $DEPLOY_JAR > /dev/null 2> /dev/null < /dev/null &