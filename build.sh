#!/bin/bash

# Build all services
./gradlew bootJar

# Build and start containers in detached mode
docker compose -f build-compose.yaml up --build -d

echo "Services are running in background. Use 'docker compose logs' to view logs."