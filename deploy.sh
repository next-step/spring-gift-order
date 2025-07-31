# --- 1. 변수 설정 ---
# 이 스크립트 파일이 있는 디렉토리를 기준으로 프로젝트 경로를 잡습니다.
PROJECT_PATH=$(dirname "$0")
JAR_NAME="spring-gift-order-0.0.1-SNAPSHOT.jar"
LOG_FILE="$PROJECT_PATH/deploy.log"

# --- 2. 로그 기록 함수 ---
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a $LOG_FILE
}

log "배포 스크립트를 시작합니다."

# --- 3. 프로젝트 최신 버전 가져오기 ---
log "Git 저장소에서 최신 코드를 가져옵니다..."
cd $PROJECT_PATH || exit
git pull

if [ $? -ne 0 ]; then
    log "Git pull에 실패했습니다. 스크립트를 중단합니다."
    exit 1
fi

# --- 4. Gradle로 프로젝트 빌드 ---
log "Gradle 빌드를 시작합니다..."
./gradlew build

if [ $? -ne 0 ]; then
    log "Gradle 빌드에 실패했습니다. 스크립트를 중단합니다."
    exit 1
fi

# --- 5. 기존 서버 프로세스 종료 ---
log "기존 서버 프로세스를 종료합니다..."
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
    log "현재 실행 중인 서버가 없습니다."
else
    log "서버를 종료합니다. (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

# --- 6. 새로운 서버 실행 ---
JAR_PATH="./build/libs/$JAR_NAME" # 상대 경로로 변경
log "새로운 서버를 실행합니다. (경로: $JAR_PATH)"
nohup java -jar -Dspring.profiles.active=prod $JAR_PATH > /dev/null 2>&1 &

sleep 2

# --- 7. 실행 확인 ---
NEW_PID=$(pgrep -f $JAR_NAME)
if [ -n "$NEW_PID" ]; then
    log "새로운 서버가 성공적으로 시작되었습니다. (PID: $NEW_PID)"
else
    log "서버 시작에 실패했습니다. nohup.out 또는 deploy.log 파일을 확인하세요."
fi

log "배포 스크립트를 종료합니다."