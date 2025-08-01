#!/bin/bash

if grep -q '^USE_EXTERNAL_DB=y' .env; then
    echo "Detected external DB configuration. Stopping Docker container..."
    docker stop spring-gift-server 2>/dev/null || echo "No running container named spring-gift-server."
else
  docker compose down 2>/dev/null || echo "No running Docker Compose services to stop."
fi
