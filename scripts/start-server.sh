#!/bin/bash

# Check if the .env file exists, if not, create it
if [ ! -f .env ]; then
    ./scripts/create-env.sh
fi

# Load environment variables from the .env file
set -o allexport
source .env
set +o allexport

# Check USE_EXTERNAL_DB value in .env
if grep -q '^USE_EXTERNAL_DB=y' .env; then
    echo "Detected external DB configuration. Running Dockerfile (docker run)..."
    docker build -t spring-gift .
    docker run --rm --name spring-gift-server --env-file .env -p 8081:8080 spring-gift
else
    echo "Detected internal DB configuration. Running Docker Compose..."
    docker compose up --build -d
fi