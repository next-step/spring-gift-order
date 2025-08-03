#!/bin/bash
set -euo pipefail

BUILD_PATH=$(ls /home/ubuntu/build/*.jar | head -n1)
if [[ -z "$BUILD_PATH" ]]; then
  echo "빌드된 JAR이 없습니다."
  exit 1
fi

JAR_NAME=$(basename "$BUILD_PATH")
CURRENT_PID=$(pgrep -f "$JAR_NAME" || true)

if [[ -n "$CURRENT_PID" ]]; then
  echo "기존 프로세스 종료 (PID: $CURRENT_PID)"
  kill -15 "$CURRENT_PID"
  sleep 3
  if ps -p "$CURRENT_PID" > /dev/null; then
    echo "graceful 종료 실패, 강제 종료"
    kill -9 "$CURRENT_PID"
  fi
else
  echo "실행 중인 애플리케이션 없음"
fi

DEPLOY_DIR=/home/ubuntu
LOG_FILE="$DEPLOY_DIR/app.log"

echo "복사 및 실행"
cp "$BUILD_PATH" "$DEPLOY_DIR/"
nohup java -jar "$DEPLOY_DIR/$JAR_NAME" --spring.profiles.active=prod > "$LOG_FILE" 2>&1 &

echo "배포 완료. 로그: $LOG_FILE"
