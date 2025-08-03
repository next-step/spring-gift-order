#!/bin/bash

JAR_FILE="spring-gift-0.0.1-SNAPSHOT.jar"
JAR_PATH="./$JAR_FILE"

echo "> 현재 실행 중인 애플리케이션을 종료합니다."
CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 실행 중인 애플리케이션이 없습니다."
else
    echo "> 실행 중인 애플리케이션(PID: $CURRENT_PID)을 종료합니다."
    kill -15 $CURRENT_PID
    sleep 5
fi

nohup java -jar \
  -Dspring.profiles.active=prod \
  -Djwt.secret="gKcuyrRVfFW0RBhpCP45mq0FPBikuPbC" \
  -Dkakao.client.id="ccdf41c4b3142b631c7f571bc6e75085" \
  -Dkakao.redirect-uri="http://3.17.12.255:8080/api/members/kakao/callback" \
  -Dkakao.provider.token-uri="https://kauth.kakao.com/oauth/token" \
  -Dkakao.provider.user-info-uri="https://kapi.kakao.com/v2/user/me" \
  -Dkakao.provider.message-memo-uri="https://kapi.kakao.com/v2/api/talk/memo/default/send" \
  "$JAR_PATH" > app.log 2>&1 &

echo "> 배포가 완료되었습니다."