BUILD_PATH=$(ls /home/ubuntu/*.jar)
JAR_NAME=$(basename $BUILD_PATH)


echo "현재 실행 중인 애플리케이션 PID 찾기: pgrep -f $JAR_NAME"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]
then
  echo ">>> 실행 중인 애플리케이션이 없습니다."
  sleep 1
else
  echo ">>> 실행 중인 애플리케이션(PID: $CURRENT_PID) 종료합니다."
  kill -15 "$CURRENT_PID"
  sleep 5
fi


echo ">>> 새 버전의 JAR 파일 복사: $BUILD_PATH -> /home/ubuntu/"
DEPLOY_PATH=/home/ubuntu/
cp "$BUILD_PATH" "$DEPLOY_PATH"

cd "$DEPLOY_PATH"


echo ">>> 새 애플리케이션 실행: java -jar $JAR_NAME"
DEPLOY_JAR="$DEPLOY_PATH$JAR_NAME"
nohup java -jar "$DEPLOY_JAR" &
