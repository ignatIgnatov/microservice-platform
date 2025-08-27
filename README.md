# Microservice Platform with Keycloak Security

## Architecture Overview

This project implements a complete microservice architecture with:

- **API Gateway**: Spring Cloud Gateway with OAuth2 security
- **Auth Service**: User authentication and social login integration
- **Ad Service**: Product/advertisement management with PostgreSQL
- **Admin Service**: Administrative functions and user management
- **Keycloak**: Identity and Access Management with social login support

## Features

- ✅ JWT-based authentication with Keycloak
- ✅ Social login (Google, Facebook)
- ✅ Role-based access control (RBAC)
- ✅ Docker containerization
- ✅ AWS deployment with EKS
- ✅ PostgreSQL database integration
- ✅ API Gateway with routing and security
- ✅ Comprehensive monitoring and health checks

## Quick Start

### Local Development

1. **Prerequisites**:
    - Java 21
    - Maven 3.8+
    - Docker & Docker Compose
    - AWS CLI (for deployment)

2. **Configuration**:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Build and Run**:
   ```bash
   chmod +x build.sh deploy-local.sh
   ./build.sh
   ./deploy-local.sh
   ```

4. **Access Services**:
    - Keycloak: http://localhost:8080 (admin/admin)
    - API Gateway: http://localhost:8081
    - Services available through gateway routing

### AWS Deployment

1. **Infrastructure Setup**:
   ```bash
   cd aws-deployment/terraform
   terraform init
   terraform plan
   terraform apply
   ```

2. **Application Deployment**:
   ```bash
   chmod +x deploy-aws.sh
   ./deploy-aws.sh
   ```

## API Endpoints

### Authentication Service (via Gateway: /auth)
- `POST /auth/register` - User registration
- `POST /auth/login` - Email/password login
- `GET /auth/social/google` - Google OAuth login
- `GET /auth/social/facebook` - Facebook OAuth login
- `POST /auth/refresh` - Refresh JWT token

### Ad Service (via Gateway: /ads)
- `GET /ads` - List all active ads
- `POST /ads` - Create new ad (authenticated)
- `PUT /ads/{id}` - Update ad (owner only)
- `DELETE /ads/{id}` - Delete ad (owner only)
- `GET /ads/user/my-ads` - User's ads

### Admin Service (via Gateway: /admin)
- `GET /admin/users` - List all users (admin only)
- `PUT /admin/users/{id}/enable` - Enable user
- `DELETE /admin/users/{id}` - Delete user
- `GET /admin/stats/dashboard` - Dashboard statistics

## Configuration

### Social Login Setup

1. **Google OAuth**:
    - Create project in Google Cloud Console
    - Enable Google+ API
    - Create OAuth 2.0 credentials
    - Update Keycloak realm configuration

2. **Facebook Login**:
    - Create Facebook app
    - Configure Facebook Login product
    - Update Keycloak realm configuration

### Database Schema

The ad-service automatically creates tables using JPA/Hibernate DDL generation.

### Security Configuration

- JWT tokens expire in 15 minutes
- Refresh tokens expire in 30 days
- RBAC with roles: `user`, `admin`, `ad-manager`
- CORS enabled for frontend integration

## Monitoring

- Spring Boot Actuator endpoints on all services
- Kubernetes health checks and readiness probes
- Application metrics available at `/actuator/metrics`

## Scaling and Performance

- Horizontal pod autoscaling configured
- Connection pooling for database
- Stateless service design
- Load balancing with AWS ALB

## Security Best Practices

- OAuth2/OIDC with Keycloak
- JWT token validation on all services
- RBAC implementation
- Secure communication between services
- Database connection encryption
- Secrets management with Kubernetes

## Troubleshooting

1. **Service won't start**: Check Keycloak availability
2. **Authentication fails**: Verify JWT issuer configuration
3. **Database connection**: Check PostgreSQL connectivity
4. **Social login issues**: Verify OAuth app configurations

## Contributing

1. Fork the repository
2. Create feature branch
3. Implement changes with tests
4. Submit pull request

## License

This project is licensed under the MIT License.