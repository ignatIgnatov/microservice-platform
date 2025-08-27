#!/bin/bash
set -e

echo "Starting local deployment with Docker Compose..."

# Create network if not exists
docker network create microservice-network 2>/dev/null || true

# Start services
docker-compose up -d --build

echo "Waiting for services to be ready..."
sleep 30

# Check service health
services=("keycloak:8080" "api-gateway:8081" "auth-service:8082" "ad-service:8083" "admin-service:8084")

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    echo "Checking $name on port $port..."
    timeout 60 bash -c "until curl -f http://localhost:$port/actuator/health 2>/dev/null || curl -f http://localhost:$port 2>/dev/null; do sleep 2; done"
    echo "$name is ready!"
done

echo "Local deployment completed successfully!"
echo "Services are available at:"
echo "- Keycloak Admin: http://localhost:8080 (admin/admin)"
echo "- API Gateway: http://localhost:8081"
echo "- Auth Service: http://localhost:8082"
echo "- Ad Service: http://localhost:8083"
echo "- Admin Service: http://localhost:8084"