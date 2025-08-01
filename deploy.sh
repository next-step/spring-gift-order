#!/bin/bash

PID=$(pgrep -f 'java .*spring-gift-0.0.1-SNAPSHOT.jar')

if [ -n "$PID" ]; then
  echo "Spring 애플리케이션 종료 중... (PID: $PID)"
  kill "$PID"
  sleep 5
else
  echo "실행 중인 Spring 애플리케이션이 없습니다."
fi

echo "Jenkins 빌드 결과 복사 중..."
JENKINS_BUILD_PATH="/var/lib/jenkins/workspace/spring-gift-order/build/libs/spring-gift-0.0.1-SNAPSHOT.jar"
TARGET_PATH="/home/ktc/spring-gift-0.0.1-SNAPSHOT.jar"

cp -f "$JENKINS_BUILD_PATH" "$TARGET_PATH"

if [ $? -eq 0 ]; then
  echo "복사 완료!"
else
  echo "복사 실패! 경로를 확인해주세요."
  exit 1
fi

echo "Spring 애플리케이션 실행 중..."
nohup java -Duser.timezone=Asia/Seoul -jar "$TARGET_PATH" > /home/deploy/nohup.out 2>&1 &
echo "애플리케이션이 백그라운드에서 실행되었습니다."
