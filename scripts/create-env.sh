#!/bin/bash

touch .env
> .env

echo "Please enter secret infos for the server"

echo "Kakao REST API key: "
read -r kakao_rest_api_key
echo "KAKAO_OAUTH_KEY=${kakao_rest_api_key}" >> .env
echo "Kakao REST API key: ${kakao_rest_api_key} is set."

echo "JWT secret key: "
read -r jwt_secret_key
echo "JWT_SECRET=${jwt_secret_key}" >> .env
echo "JWT secret key: ${jwt_secret_key} is set."

echo "JWT expires in (seconds): "
read -r jwt_expires_in
echo "JWT_EXPIRATION=${jwt_expires_in}" >> .env
echo "JWT expires in: ${jwt_expires_in} seconds is set."

echo "Please enter the address and port to bind the server to."

echo "Bind Address (e.g., 0.0.0.0, 127.0.0.1, localhost): "
read -r bind_address
echo "SERVER_HOST=${bind_address}" >> .env
echo "Bind Address: '${bind_address}' is set."

echo "Bind Port (e.g., 8080, 3000): "
read -r bind_port
echo "SERVER_PORT=${bind_port}" >> .env
echo "Bind Port: '${bind_port}' is set."

echo "Do you want to use an external database? (y/n)"
read -r use_external_db

echo "USE_EXTERNAL_DB=${use_external_db}" >> .env

if [ "$use_external_db" == "y" ]; then
    echo "Please enter the database configuration infos:"

    echo "Database Host: "
    read -r db_host
    echo "DB_HOST=${db_host}" >> .env
    echo "Database Host: ${db_host} is set."

    echo "Database Port: "
    read -r db_port
    echo "DB_PORT=${db_port}" >> .env
    echo "Database Port: ${db_port} is set."

    echo "Database User: "
    read -r db_user
    echo "DB_USER=${db_user}" >> .env
    echo "Database User: ${db_user} is set."

    echo "Database Password: "
    read -r db_password
    echo "DB_PASSWORD=${db_password}" >> .env
    echo "Database Password: ${db_password} is set."

    echo "Database Name: "
    read -r db_name
    echo "DB_NAME=${db_name}" >> .env
    echo "Database Name: ${db_name} is set."
fi
