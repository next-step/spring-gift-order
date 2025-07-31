#!/bin/bash

PROJECT_PATH=/home/ubuntu/spring-gift-order
GIT_BRANCH=step-3
EXTERNAL_CONFIG_PATH="/home/ubuntu/config/application.yaml"

echo "🚀 배포 시작!"

# 현재 디렉토리를 프로젝트 경로로 변경
cd $PROJECT_PATH

echo "Git 저장소에서 최신 코드를 가져옵니다."
git pull origin $GIT_BRANCH

echo "🚀 빌드 시작!"
./gradlew clean build -x test

echo "로그 파일 경로를 설정합니다."
LOG_PATH=$PROJECT_PATH/logs
mkdir -p $LOG_PATH

echo "🚀 애플리케이션 시작!"

BUILD_PATH=$(ls $PROJECT_PATH/build/libs/spring-gift-*.jar | head -n 1)
JAR_NAME=$(basename $BUILD_PATH)

echo "JAR 파일: $JAR_NAME"

CURRENT_PID=$(pgrep -f "java.*$JAR_NAME")

if [ -z "$CURRENT_PID" ]
then
  echo "ℹ️ 실행 중인 애플리케이션이 없습니다."
  sleep 1
else
  echo "❗ 기존 애플리케이션을 종료합니다. (PID: $CURRENT_PID)"
  kill -15 $CURRENT_PID
  sleep 5
  
  # 강제 종료가 필요한 경우
  CURRENT_PID=$(pgrep -f "java.*$JAR_NAME")
  if [ ! -z "$CURRENT_PID" ]; then
    echo "❗ 강제 종료합니다. (PID: $CURRENT_PID)"
    kill -9 $CURRENT_PID
    sleep 2
  fi
fi

# 외부 설정 파일이 있는 경우에만 사용
if [ -f "$EXTERNAL_CONFIG_PATH" ]; then
  nohup java -jar $BUILD_PATH --spring.profiles.active=prod --spring.config.location=classpath:/application.yaml,$EXTERNAL_CONFIG_PATH > $LOG_PATH/app.log 2>&1 &
else
  echo "⚠️ 외부 설정 파일이 없습니다. 기본 설정으로 시작합니다."
  nohup java -jar $BUILD_PATH --spring.profiles.active=prod > $LOG_PATH/app.log 2>&1 &
fi

NEW_PID=$!
echo "✅ 새 애플리케이션이 시작되었습니다. (PID: $NEW_PID)"

# 애플리케이션 시작 확인
sleep 3
if ps -p $NEW_PID > /dev/null; then
  echo "🎉 배포가 완료되었습니다 🎉"
else
  echo "❌ 애플리케이션 시작에 실패했습니다. 로그를 확인하세요."
  tail -20 $LOG_PATH/app.log
  exit 1
fi
