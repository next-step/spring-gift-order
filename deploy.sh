  #!/bin/bash

  echo "===> 배포 시작"

  ./gradlew clean build -x test
  if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패"
    exit 1
  fi

  JAR_PATH=$(ls ./build/libs/*.jar | grep -v plain | head -n 1)
  JAR_NAME=$(basename "$JAR_PATH")

  echo "빌드된 JAR 파일: $JAR_NAME"

  PID=$(pgrep -f "$JAR_NAME")
  if [ -n "$PID" ]; then
    echo "기존 프로세스 종료 중 (PID: $PID)"
    kill -15 "$PID"
    sleep 5
  else
    echo "기존 프로세스 없음"
  fi

  DEPLOY_DIR="/home/ubuntu/app"
  mkdir -p $DEPLOY_DIR
  cp "$JAR_PATH" "$DEPLOY_DIR/"

  cd $DEPLOY_DIR

  echo "애플리케이션 실행 중..."
  nohup java -jar "$JAR_NAME" > app.log 2>&1 &

  sleep 5

  NEW_PID=$(pgrep -f "$JAR_NAME")
  if [ -n "$NEW_PID" ]; then
    echo "✅ 배포 성공 (PID: $NEW_PID)"
  else
    echo "❌ 배포 실패. 로그 확인 필요."
  fi
