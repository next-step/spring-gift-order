#!/bin/bash

BUILD_PATH=$(ls /home/ec2-user/build/*SNAPSHOT.jar | grep -v plain)
JAR_NAME=$(basename $BUILD_PATH)
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "✅ 실행 중인 애플리케이션 없음"
else
  echo "🛑 실행 중인 애플리케이션 종료: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_PATH=/home/ec2-user
DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME

echo "🚀 JAR 복사 및 실행"
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH
nohup java -jar $DEPLOY_JAR > nohup.out 2>&1 &
