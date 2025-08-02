#!/bin/bash
BUILD_PATH=$(ls /home/ubuntu/*.jar | grep -v 'plain')
JAR_NAME=$(basename $BUILD_PATH)

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "실행 중인 애플리케이션이 없습니다."
  sleep 1
else
  echo "기존 애플리케이션 (PID: $CURRENT_PID) 종료 중..."
  kill -15 $CURRENT_PID
  sleep 5
  echo "기존 애플리케이션 종료 완료."
fi

DEPLOY_PATH=/home/ubuntu/spring-gift/
echo "JAR 파일 ($JAR_NAME)을 $DEPLOY_PATH 로 복사 중..."
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "새로운 애플리케이션 ($DEPLOY_JAR) 시작 중..."
nohup java -jar "$DEPLOY_JAR" > app.log 2>&1 &
echo "애플리케이션 시작 완료. 로그 -> $DEPLOY_PATH/app.log"
