#!/bin/bash
set -e

echo "Building microservice platform..."

# Build all services
services=("api-gateway" "auth-service" "ad-service" "admin-service")

for service in "${services[@]}"; do
    echo "Building $service..."
    cd $service
    mvn clean package -DskipTests
    docker build -t $service:latest .
    cd ..
done

echo "All services built successfully!"