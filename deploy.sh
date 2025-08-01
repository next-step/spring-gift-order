#!/bin/bash
set -e

APP_DIR=${1:-"$PWD"}
DEPLOY_DIR=${2:-"$PWD/deploy"}

echo ">>> APP_DIR = $APP_DIR"
echo ">>> DEPLOY_DIR = $DEPLOY_DIR"

# cd $APP_DIR && git pull origin step3

cd $APP_DIR
./gradlew clean bootJar

JAR_PATH=$(ls build/libs/*.jar | sort | tail -n1)
JAR_NAME=$(basename "$JAR_PATH")

OLD_PID=$(pgrep -f "$JAR_NAME" || true)
if [ -n "$OLD_PID" ]; then
  echo "Stopping old process ($OLD_PID)…"
  kill -15 $OLD_PID
  sleep 10
fi

mkdir -p "$DEPLOY_DIR"
cp "$JAR_PATH" "$DEPLOY_DIR/"

cd "$DEPLOY_DIR"
LOG_FILE="$DEPLOY_DIR/deploy.log"
nohup java -jar "$JAR_NAME" >"$LOG_FILE" 2>&1 &
echo "Started $JAR_NAME, logs → $LOG_FILE"
