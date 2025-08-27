🏗️ Complete Architecture
Services Included:

API Gateway: Spring Cloud Gateway with OAuth2 security and routing
Auth Service: User registration, login, and social authentication
Ad Service: Product/advertisement management with PostgreSQL
Admin Service: User management and administrative functions
Keycloak: Identity provider with social login support

🔐 Security Features

JWT-based authentication with Keycloak
Social login support for Google and Facebook
Role-based access control (user and admin roles)
OAuth2 resource server configuration
Secure service-to-service communication

🐳 Docker & Deployment
Local Development:

Complete docker-compose.yml for local development
Automated build scripts
Health checks and service dependencies

AWS Production Deployment:

Terraform infrastructure as code
EKS cluster with node groups
RDS PostgreSQL database
Application Load Balancer
Kubernetes manifests with proper scaling and monitoring

🚀 Key Features

Authentication Options:

Email/password registration and login
Google OAuth integration
Facebook OAuth integration
JWT token refresh mechanism


Ad Management:

CRUD operations for advertisements
User-specific ad management
Category-based filtering
PostgreSQL persistence with JPA


Admin Functions:

User management (enable/disable/delete)
System statistics dashboard
Cross-service data aggregation


Production Ready:

Health checks and monitoring
Horizontal scaling configuration
Database connection pooling
Comprehensive error handling



📋 Quick Start

Local Development:
bash./build.sh          # Build all services
./deploy-local.sh   # Start with Docker Compose

AWS Deployment:
bashcd aws-deployment/terraform
terraform apply     # Create infrastructure
./deploy-aws.sh     # Deploy applications


🔧 Configuration Required
Before deployment, you'll need to:

Set up OAuth apps in Google and Facebook developer consoles
Configure social login in the Keycloak realm export
Update AWS credentials and region settings
Set database passwords in secrets

The architecture is designed to be production-ready with proper security, scalability, and monitoring. Each service is independently deployable and follows microservice best practices with clear separation of concerns.