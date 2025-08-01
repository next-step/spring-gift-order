#!/bin/bash

GIT_URL=https://github.com/Sunja-An/spring-gift-order.git
GIT_BRANCH_NAME=sunja-an

BUILD_PATH=$(ls /home/ubuntu/deploy/app.jar)
JAR_NAME=$(basename $BUILD_PATH)

echo "> 🚀 NEW APPLICATION: $JAR_NAME"

LOG_FILE="/home/ubuntu/app.log"

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
    echo "> NO APPLICATION 🥲"
else
    echo "> 🙇 QUIT APPLICATION (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

DEPLOY_PATH=/home/ubuntu/
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

echo "> 📚 새 애플리케이션 배포"
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR > $LOG_FILE 2>&1 &

echo "> 🚀 배포 완료"
