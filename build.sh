#!/bin/sh

PROJECT_PATH="/home/ubuntu/spring-gift-order"
BUILD_PATH="$PROJECT_PATH/build/libs"
DEPLOY_PATH="/home/ubuntu/build"

OLD_JAR=$(ls "$DEPLOY_PATH"/*.jar || true)
if [ -n "$OLD_JAR" ]
then
  PID=$(pgrep -f "$(basename "$OLD_JAR")")
  [ -n "$PID" ] && kill -15 "$PID" && sleep 5
else
  sleep 1
fi

cd "$PROJECT_PATH"
./gradlew clean build -x test

DEPLOY_JAR=$(ls -t "$BUILD_PATH"/*.jar | grep -v -- '-plain.jar')
cp "$DEPLOY_JAR" "$DEPLOY_PATH/"
cd "$DEPLOY_PATH"

nohup java -Dspring.profiles.active=prod -jar $(basename "$DEPLOY_JAR") > /dev/null 2> /dev/null < /dev/null &