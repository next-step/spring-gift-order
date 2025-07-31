#!/bin/bash
# environment variables setup

echo "Please enter secret infos for the server"

echo "Kakao REST API key: "
read -r kakao_rest_api_key
export KAKAO_OAUTH_KEY=$kakao_rest_api_key
echo "Kakao REST API key: ${kakao_rest_api_key} is set."

echo "JWT secret key: "
read -r jwt_secret_key
export JWT_SECRET=$jwt_secret_key
echo "JWT secret key: ${jwt_secret_key} is set."

echo "JWT expires in (seconds): "
read -r jwt_expires_in
export JWT_EXPIRATION=$jwt_expires_in
echo "JWT expires in: ${jwt_expires_in} seconds is set."

echo "Please enter the address and port to bind the server to."

echo "Bind Address (e.g., 0.0.0.0, 127.0.0.1, localhost): "
read -r bind_address
export SERVER_HOST=$bind_address
echo "Bind Address: '${bind_address}' is set."

echo "Bind Port (e.g., 8080, 3000): "
read -r bind_port
export SERVER_PORT=$bind_port
echo "Bind Port: '${bind_port}' is set."

echo "Do you want to use an external database? (y/n)"
read -r use_external_db

if [ "$use_external_db" == "y" ]; then
    echo "Please enter the database configuration infos:"

    echo "Database Host: "
    read -r db_host
    export DB_HOST=$db_host
    echo "Database Host: ${db_host} is set."

    echo "Database Port: "
    read -r db_port
    export DB_PORT=$db_port
    echo "Database Port: ${db_port} is set."

    echo "Database User: "
    read -r db_user
    export DB_USER=$db_user
    echo "Database User: ${db_user} is set."

    echo "Database Password: "
    read -r db_password
    export DB_PASSWORD=$db_password
    echo "Database Password: ${db_password} is set."

    echo "Database Name: "
    read -r db_name
    export DB_NAME=$db_name
    echo "Database Name: ${db_name} is set."

    docker run --rm \
        --name gift-backend-server \
        -p 8081:8080 \
        -e KAKAO_OAUTH_KEY="$KAKAO_OAUTH_KEY" \
        -e JWT_SECRET="$JWT_SECRET" \
        -e JWT_EXPIRATION="$JWT_EXPIRATION" \
        -e DB_HOST="$DB_HOST" \
        -e DB_PORT="$DB_PORT" \
        -e DB_USER="$DB_USER" \
        -e DB_PASSWORD="$DB_PASSWORD" \
        -e DB_NAME="$DB_NAME" \
        -e SERVER_HOST="$SERVER_HOST" \
        -e SERVER_PORT="$SERVER_PORT" \
        kakao-reset-server:latest

else
    echo "Using internal database configuration."
    docker compose up
fi
