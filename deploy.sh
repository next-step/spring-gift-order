#!/bin/bash

# =================================================================
# 1. 변수 설정
# =================================================================
# 새로 빌드한 JAR 파일이 위치한 경로
BUILD_PATH=$(ls /home/ubuntu/spring-gift-order/build/libs/*.jar)
# JAR 파일의 이름
JAR_NAME=$(basename $BUILD_PATH)
# 배포할 경로
DEPLOY_PATH=/home/ubuntu/app/
# 실행할 JAR 파일 경로
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME

# =================================================================
# 2. 기존 애플리케이션 종료
# =================================================================
# 현재 실행 중인 프로세스의 ID(PID)를 찾습니다.
CURRENT_PID=$(pgrep -f $JAR_NAME)

# 프로세스가 실행 중인지 확인하고, 실행 중이면 종료(kill)합니다.
if [ -z "$CURRENT_PID" ]
then
  echo "> 현재 실행 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> 실행 중인 애플리케이션을 종료합니다. (PID: $CURRENT_PID)"
  kill -15 $CURRENT_PID
  sleep 5 # 애플리케이션이 완전히 종료될 때까지 5초간 대기합니다.
fi

# =================================================================
# 3. 새 애플리케이션 배포 및 실행
# =================================================================
echo "> 새 애플리케이션을 배포합니다."
# 빌드된 JAR 파일을 배포 경로로 복사합니다.
cp $BUILD_PATH $DEPLOY_PATH

echo "> $DEPLOY_JAR 를 실행합니다."
# -Dspring.profiles.active=prod 옵션을 추가하여 prod 프로필로 실행합니다.
nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR > $DEPLOY_PATH/application.log 2>&1 &
