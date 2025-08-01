#!/bin/bash

APP_PATH=$(pwd)
BUILD_PATH="${APP_PATH}/build/libs"
GIT_REMOTE=origin
GIT_BRANCH=flareseek
APP_PORT=80

function print_log() {
  echo "=================================================="
  echo "[DEPLOY] $1"
}

print_log "Fetching files from Github"
git pull ${GIT_REMOTE} ${GIT_BRANCH}

print_log "Building the application..."
./gradlew clean build

JAR_NAME=$(ls ${BUILD_PATH} | grep .jar$ | grep -v "plain")
JAR_PATH="${BUILD_PATH}/${JAR_NAME}"

print_log "Stopping the application on port ${APP_PORT}"
PID=$(lsof -ti :${APP_PORT} | grep java)
if [ -n "$PID" ]; then
  print_log "killint process with PID: ${PID}"
  kill -9 $PID
fi

if [ -f "$JAR_PATH" ]; then
  print_log "Starting new application..."
  nohup java -jar -Dserver.port=${APP_PORT} "$JAR_PATH" > /dev/null 2>&1 &
  print_log "Deployment successful."
else
  print_log "Build failed. JAR file not found at $JAR_PATH"
  exit 1
fi


