#!/bin/bash

# EC2 접속 정보
USER=ubuntu
HOST=52.79.215.198
KEY_PATH=/c/Users/hyunseo/Downloads/newkeypair.pem
TARGET_DIR=/home/ubuntu

# 로컬 프로젝트 빌드
echo "[STEP 1] Gradle 빌드 시작"
./gradlew clean build || { echo "빌드 실패"; exit 1; }

# 빌드 결과물 확인
JAR_FILE=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)
JAR_NAME=$(basename "$JAR_FILE")
echo "[STEP 2] 빌드 결과물: $JAR_NAME"

# EC2로 JAR 파일 전송
echo "[STEP 3] EC2로 파일 업로드 중..."
scp -i "$KEY_PATH" "$JAR_FILE" "$USER@$HOST:$TARGET_DIR/" || { echo "업로드 실패"; exit 1; }

# EC2 원격 실행
echo "[STEP 4] EC2에서 애플리케이션 재시작"
ssh -i "$KEY_PATH" "$USER@$HOST" <<EOF
  cd $TARGET_DIR

  echo "[EC2] 기존 애플리케이션 프로세스 종료 시도"
  PID=\$(pgrep -f $JAR_NAME)
  if [ -n "\$PID" ]; then
    echo "[EC2] 프로세스 종료: \$PID"
    kill -15 \$PID
    sleep 2
  else
    echo "[EC2] 실행 중인 프로세스 없음"
  fi

  echo "[EC2] 애플리케이션 실행"
  nohup java -jar $JAR_NAME > app.log 2>&1 &
EOF

echo "[완료] 배포가 완료되었습니다."
