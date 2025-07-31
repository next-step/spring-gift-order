#!/bin/bash

PROJECT_PATH=/home/ubuntu/spring-gift-order
GIT_BRANCH=step3
EXTERNAL_CONFIG_PATH="/home/ubuntu/config/application-prod.properties"

BUILD_PATH=$(ls $PROJECT_PATH/build/libs/spring-gift-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_PATH)

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "ℹ️ 실행 중인 애플리케이션이 없습니다."
  sleep 1
else
  echo "❗ 기존 애플리케이션을 종료합니다. (PID: $CURRENT_PID)"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "🚀 배포 시작!"

echo "Git 저장소에서 최신 코드를 가져옵니다."

git pull origin $GIT_BRANCH
./gradlew build -x test

LOG_PATH=$PROJECT_PATH/logs
mkdir -p $LOG_PATH

echo "🚀 애플리케이션 시작!"

nohup java -jar $BUILD_PATH --spring.profiles.active=prod --spring.config.location=classpath:/application.properties,$EXTERNAL_CONFIG_PATH > $LOG_PATH/app.log 2>&1 &

echo "🎉 배포가 완료되었습니다 🎉"