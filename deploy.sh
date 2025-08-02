echo "빌드 파일 경로 확인"
KAKAO_REST_API_KEY=$1
KAKAO_REDIRECT_URL=$2
BUILD_PATH=$(ls /home/ubuntu/build/*.jar)
JAR_NAME=$(basename $BUILD_PATH)

echo "현재 실행중인 애플링케이션 pid 확인"
CURRENT_PID=$(pgrep -f $JAR_NAME | head -n 1)
echo "현재 pid: $CURRENT_PID"
if [ -z $CURRENT_PID ]
then
        sleep 1
else
        sudo kill -15 $CURRENT_PID
        sleep 5
fi

DEPLOY_PATH=/home/ubuntu/
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "JAR 파일 실행"
nohup java -jar $DEPLOY_JAR --kakao.rest-api-key=$KAKAO_REST_API_KEY --kakao.redirect-uri=$KAKAO_REDIRECT_URL > /dev/null 2> /dev/null < /dev/null &4
echo "JAR 파일 실행완료"