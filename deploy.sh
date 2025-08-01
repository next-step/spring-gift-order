#!/bin/bash

GIT_URL=https://github.com/Sunja-An/spring-gift-order.git
GIT_BRANCH_NAME=sunja-an
PROJECT_PATH=/home/ubuntu/spring-gift-order

echo "> 🚀 Deploy Start"

cd $PROJECT_PATH

echo "> 🔨 Build Start"
./gradlew clean build -x test

LOG_PATH=$PROJECT_PATH/logs
mkdir -p $LOG_PATH

# -plain.jar 제외하고 가장 최근 jar 선택
BUILD_PATH=$(ls $PROJECT_PATH/build/libs/spring-gift-*.jar | grep -v 'plain' | head -n1)
JAR_NAME=$(basename $BUILD_PATH)

echo "> 🚀 NEW APPLICATION: $JAR_NAME"

LOG_FILE="/home/ubuntu/app.log"

# 현재 실행 중인 프로세스 종료
CURRENT_PID=$(pgrep -f "java.*$JAR_NAME")
if [ -z "$CURRENT_PID" ]; then
    echo "> NO APPLICATION 🥲"
else
    echo "> 🙇 QUIT APPLICATION (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

# 배포
DEPLOY_PATH=/home/ubuntu/
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

echo "> 📚 새 애플리케이션 배포"
DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME

nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR > $LOG_FILE 2>&1 &

echo "> 🚀 배포 완료"
