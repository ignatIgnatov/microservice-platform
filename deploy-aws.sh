#!/bin/bash
set -e

# Configuration
AWS_REGION="us-west-2"
ECR_REPOSITORY_PREFIX="your-account-id.dkr.ecr.${AWS_REGION}.amazonaws.com"
CLUSTER_NAME="microservice-cluster"

echo "Deploying to AWS..."

# Login to ECR
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REPOSITORY_PREFIX

# Build and push images
services=("api-gateway" "auth-service" "ad-service" "admin-service")

for service in "${services[@]}"; do
    echo "Building and pushing $service..."

    # Tag and push to ECR
    docker tag $service:latest $ECR_REPOSITORY_PREFIX/$service:latest
    docker push $ECR_REPOSITORY_PREFIX/$service:latest
done

# Update kubeconfig
aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME

# Apply Kubernetes configurations
echo "Applying Kubernetes configurations..."

kubectl apply -f aws-deployment/k8s/namespace.yaml
kubectl apply -f aws-deployment/k8s/secrets.yaml
kubectl apply -f aws-deployment/k8s/rds-secret.yaml
kubectl apply -f aws-deployment/k8s/postgres-deployment.yaml
kubectl apply -f aws-deployment/k8s/keycloak-deployment.yaml

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/keycloak -n microservice-platform

# Deploy application services
kubectl apply -f aws-deployment/k8s/api-gateway-deployment.yaml
kubectl apply -f aws-deployment/k8s/auth-service-deployment.yaml
kubectl apply -f aws-deployment/k8s/ad-service-deployment.yaml
kubectl apply -f aws-deployment/k8s/admin-service-deployment.yaml
kubectl apply -f aws-deployment/k8s/ingress.yaml

# Wait for all deployments
echo "Waiting for all services to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment --all -n microservice-platform

echo "AWS deployment completed successfully!"
echo "Check service status with: kubectl get pods -n microservice-platform"