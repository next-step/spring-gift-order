#!/bin/bash

sudo apt update -yy && sudo apt upgrade -yy

# Install docker

sudo apt-get update
sudo apt-get install docker.io
sudo service docker start

# Install docker-compose

sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose



echo "Please enter secret infos for the server"

echo "kakao reset api key : "
read -r kakao_reset_api_key
export KAKAO_OAUTH_KEY=$kakao_reset_api_key

echo "JWT secret key : "
read -r jwt_secret_key
export JWT_SECRET=$jwt_secret_key

echo "JWT expries in (seconds) : "
read -r jwt_expires_in
export JWT_EXPIRATION=$jwt_expires_in

echo "Do you want to use an external database? (y/n)"
read -r use_external_db
if [ "$use_external_db" == "y" ]; then
    echo "Please enter the database configuration infos:"

    echo: "Database Host : "
    read db_host
    export DB_HOST=$db_host

    echo "Database Port : "
    read db_port
    export DB_PORT=$db_port

    echo "Database User : "
    read db_user
    export DB_USER=$db_user

    echo "Database Password : "
    read db_password
    export DB_PASSWORD=$db_password

    echo "Database Name : "
    read db_name
    export DB_NAME=$db_name

    podman run \
        --name gift-backend-server \
        -p 8080:8080 \
        -e KAKAO_OAUTH_KEY=$KAKAO_OAUTH_KEY \
        -e JWT_SECRET=$JWT_SECRET \
        -e JWT_EXPIRATION=$JWT_EXPIRATION \
        -e DB_HOST=$DB_HOST \
        -e DB_PORT=$DB_PORT \
        -e DB_USER=$DB_USER \
        -e DB_PASSWORD=$DB_PASSWORD \
        -e DB_NAME=$DB_NAME \
        kakao-reset-server:latest
else
    echo "Using internal database configuration."
    podman-compose up
fi