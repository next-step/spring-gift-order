#!/bin/bash

set -e

REPO_URL=https://github.com/gary5876/spring-gift-order.git
APP_NAME=spring-gift
BRANCH_NAME=step3
APP_DIR=/home/ubuntu/$APP_NAME

echo "=실행 중인 애플리케이션 종료="
PID=$(pgrep -f '.jar')
if [ -n "$PID" ]; then
  echo ">> 프로세스 종료: $PID"
  kill -15 $PID
  sleep 5
else
  echo ">> 실행 중인 프로세스 없음"
fi

echo "=Git pull or clone="
if [ -d "$APP_DIR" ]; then
  cd $APP_DIR
  git reset --hard
  git checkout $BRANCH_NAME
  git pull origin $BRANCH_NAME
else
  git clone -b $BRANCH_NAME $REPO_URL $APP_DIR
  cd $APP_DIR
fi

echo "=Gradle 빌드="
./gradlew clean build

echo "=애플리케이션 실행 ="
JAR_NAME=$(ls *SNAPSHOT.jar | grep -v plain | head -n 1)

nohup java -jar $JAR_NAME > log.txt 2>&1 &
echo ">> 실행 완료: $JAR_NAME"
