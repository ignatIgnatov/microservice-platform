# QHTI.BG Boat Marketplace

Complete boat marketplace application supporting boats, jet skis, trailers, engines, marine electronics, fishing equipment, parts and services.

## Features

### Supported Categories
- **Лодки и Яхти** (Boats & Yachts) - Motor boats, sailing boats, kayaks/canoes
- **Джетове** (Jet Skis) - Personal watercraft with full specifications
- **Колесари** (Trailers) - Boat and jet ski trailers
- **Двигатели** (Engines) - Outboard and inboard marine engines
- **Морска Електроника** (Marine Electronics) - Sonars, depth finders, trolling motors
- **Риболов** (Fishing) - Rods, reels, lines, hooks, lures, bait
- **Части** (Parts) - Hoses, brackets, propellers, impellers, covers
- **Услуги** (Services) - Boat repair, engine repair services

### Core Functionality
- **User Authentication** - JWT-based authentication via Keycloak
- **Advanced Search** - Category-specific filters with mandatory field validation
- **Price Management** - Fixed prices, free, negotiable, barter options
- **Multi-language Support** - Bulgarian interface following QHTI.BG specifications
- **Location-based Search** - Bulgarian regions, cities, and villages
- **Image Support** - Multiple images per ad (up to 10, max 5MB each)
- **Featured Ads** - Promoted listings with enhanced visibility
- **Statistics & Analytics** - Market insights and user metrics

## Technology Stack

### Backend
- **Spring Boot 3.2** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Security** - OAuth2 Resource Server with JWT
- **Spring Data R2DBC** - Reactive database access
- **PostgreSQL** - Primary database
- **Keycloak** - Authentication and authorization
- **Docker & Docker Compose** - Containerization

### API Documentation
- **OpenAPI 3.0** - API specification
- **Swagger UI** - Interactive API documentation

### Testing
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing with real databases

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### Local Development Setup

1. **Clone the repository**
```bash
git clone https://github.com/your-org/boat-marketplace.git
cd boat-marketplace
```

2. **Start infrastructure services**
```bash
docker-compose up -d postgres keycloak redis
```

3. **Configure Keycloak**
- Access Keycloak at http://localhost:8080
- Login with admin/admin
- Create realm: `boat-marketplace`
- Create client: `boat-marketplace-client`
- Configure JWT settings

4. **Set environment variables**
```bash
export KEYCLOAK_AUTH_SERVER_URL=http://localhost:8080
export KEYCLOAK_REALM=boat-marketplace
export KEYCLOAK_CLIENT_SECRET=your-client-secret
export DB_PASSWORD=boat_password
```

5. **Run the application**
```bash
./mvnw spring-boot:run
```

6. **Access the application**
- API: http://localhost:8082/api/v1/boats
- Swagger UI: http://localhost:8082/swagger-ui.html
- Health Check: http://localhost:8082/actuator/health

### Docker Deployment

**Full stack deployment:**
```bash
docker-compose up -d
```

**Production deployment:**
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## API Documentation

### Authentication
All write operations require JWT authentication. Include the token in the Authorization header:
```bash
Authorization: Bearer <jwt-token>
```

### Core Endpoints

#### Create Advertisements
```bash
# Create boat ad
POST /api/v1/boats/boats
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Красива моторна лодка",
  "description": "Отлична лодка в перфектно състояние...",
  "category": "BOATS_AND_YACHTS",
  "price": {
    "amount": 25000.00,
    "type": "FIXED_PRICE",
    "includingVat": true
  },
  "location": "Варна",
  "adType": "FROM_PRIVATE",
  "boatSpec": {
    "type": "MOTOR_BOAT",
    "brand": "Sea Ray",
    "model": "Sundancer 280",
    "horsepower": 350,
    "length": 8.5,
    "year": 2020,
    ...
  }
}
```

#### Search Advertisements
```bash
# Advanced search
POST /api/v1/boats/search
Content-Type: application/json

{
  "category": "BOATS_AND_YACHTS",
  "minPrice": 10000,
  "maxPrice": 50000,
  "location": "Варна",
  "sortBy": "PRICE_LOW_TO_HIGH",
  "boatFilters": {
    "boatType": "MOTOR_BOAT",
    "minHorsepower": 200,
    "maxLength": 10.0
  }
}
```

#### Browse by Category
```bash
# Get boats
GET /api/v1/boats/category/BOATS_AND_YACHTS?sortBy=NEWEST

# Get jet skis  
GET /api/v1/boats/category/JET_SKIS?sortBy=PRICE_LOW_TO_HIGH

# Get trailers
GET /api/v1/boats/category/TRAILERS
```

### Response Format
```json
{
  "id": 1,
  "title": "Красива моторна лодка",
  "category": "BOATS_AND_YACHTS",
  "price": {
    "amount": 25000.00,
    "type": "FIXED_PRICE",
    "includingVat": true
  },
  "location": "Варна",
  "userEmail": "user@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "active": true,
  "viewsCount": 45,
  "boatSpec": {
    "type": "MOTOR_BOAT",
    "brand": "Sea Ray",
    "horsepower": 350,
    "length": 8.5,
    "year": 2020
  },
  "formattedPrice": "25,000 лв с ДДС",
  "userFullName": "Иван Петров"
}
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8082 |
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | boat_marketplace |
| `DB_USERNAME` | Database user | boat_user |
| `DB_PASSWORD` | Database password | boat_password |
| `KEYCLOAK_AUTH_SERVER_URL` | Keycloak URL | http://localhost:8080 |
| `KEYCLOAK_REALM` | Keycloak realm | boat-marketplace |
| `KEYCLOAK_CLIENT_SECRET` | Client secret | - |
| `AUTH_SERVICE_URL` | Auth service URL | http://localhost:8081 |

### Application Properties

**Development (`application-dev.yml`):**
```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/boat_marketplace_dev
  sql:
    init:
      mode: always

logging:
  level:
    com.platform.ads: DEBUG
```

**Production (`application-prod.yml`):**
```yaml
spring:
  r2dbc:
    pool:
      initial-size: 10
      max-size: 50

logging:
  level:
    com.platform.ads: WARN
    root: ERROR
```

## Database Schema

### Main Tables
- `ads` - Core advertisement data
- `boat_specifications` - Boat-specific details
- `jetski_specifications` - Jet ski specifications
- `trailer_specifications` - Trailer details
- `engine_specifications` - Engine specifications
- `boat_interior_features` - Interior features (many-to-many)
- `boat_exterior_features` - Exterior features (many-to-many)
- `boat_equipment` - Equipment (many-to-many)

### Indexes
Performance-optimized indexes for:
- Category-based searches
- Price range queries
- Location-based filtering
- User-specific queries
- Full-text search on title/description

## Business Rules

### Mandatory Fields by Category

**Boats & Yachts:**
- Type, Brand, Model, Engine Type, Horsepower, Length, Width, Max People, Year, Weight, Fuel Capacity, Registration, Condition

**Jet Skis:**
- Brand, Model, Registration, Horsepower, Year, Weight, Fuel Capacity, Operating Hours, Fuel Type, Trailer Included, Warranty, Condition

**Trailers:**
- Type, Brand, Axle Count, Registration, Load Capacity, Length, Width, Year, Warranty, Condition

**Engines:**
- Type, Brand, Stroke Type, Warranty, Horsepower, Operating Hours, Year, Fuel Capacity, Ignition Type, Control Type, Shaft Length, Fuel Type, System Type, Condition, Color

### Validation Rules
- Title: 5-30 characters
- Description: 20-2000 characters
- Quick Description: max 210 characters
- Price: positive numbers for fixed prices
- Year: 1900 to current year + 1
- Horsepower: 1-10000 for boats, 1-1000 for jet skis

### Search Behavior
- Category selection is mandatory
- Empty filters show all results in category
- Single filter application shows results matching that filter
- Price ranges: "Най-ниска цена" to "Най-висока цена"
- Sort options: Price (low/high), Date (new/old), Views (most viewed)

## Testing

### Unit Tests
```bash
./mvnw test
```

### Integration Tests
```bash
./mvnw test -Dtest=**/*IntegrationTest
```

### API Testing with curl
```bash
# Health check
curl http://localhost:8082/actuator/health

# Search boats
curl -X POST http://localhost:8082/api/v1/boats/search \
  -H "Content-Type: application/json" \
  -d '{"category": "BOATS_AND_YACHTS"}'

# Get specific ad
curl http://localhost:8082/api/v1/boats/1
```

## Monitoring & Observability

### Health Checks
- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Logging
- Structured JSON logging in production
- Request/response logging with correlation IDs
- Performance metrics for database queries
- Security events logging

## Security

### Authentication
- JWT tokens from Keycloak
- Token validation on protected endpoints
- Automatic token refresh support

### Authorization
- User-based access control
- Ad ownership validation
- Rate limiting on API endpoints

### Data Protection
- Input validation and sanitization
- SQL injection prevention via R2DBC
- XSS protection
- CORS configuration

## Performance Considerations

### Database Optimization
- Optimized indexes for search queries
- Connection pooling (5-50 connections)
- Query timeout configuration
- Database health monitoring

### Caching Strategy
- Redis for frequently accessed data
- 15-minute TTL for search results
- User session caching
- Static content caching

### Scalability
- Reactive programming model
- Non-blocking I/O operations
- Horizontal scaling support
- Load balancer ready

## Troubleshooting

### Common Issues

**Application won't start:**
```bash
# Check database connection
docker-compose ps
docker-compose logs postgres

# Verify Keycloak is running
curl http://localhost:8080/health
```

**Authentication errors:**
```bash
# Verify JWT configuration
curl http://localhost:8080/realms/boat-marketplace/.well-known/openid_configuration

# Check client configuration in Keycloak admin console
```

**Database connection errors:**
```bash
# Reset database
docker-compose down -v
docker-compose up -d postgres
./mvnw spring-boot:run
```

### Debug Mode
```bash
# Enable debug logging
export LOGGING_LEVEL_COM_PLATFORM_ADS=DEBUG
./mvnw spring-boot:run

# Or use application-debug.yml
./mvnw spring-boot:run -Dspring.profiles.active=debug
```

## Contributing

### Development Workflow
1. Create feature branch from `main`
2. Implement feature with tests
3. Run full test suite: `./mvnw clean test`
4. Update documentation
5. Submit pull request

### Code Standards
- Java 21 features encouraged
- Reactive programming patterns
- Comprehensive error handling
- Input validation on all endpoints
- Integration tests for new features

### Database Changes
- Create migration scripts in `src/main/resources/db/migration/`
- Test with both PostgreSQL and H2
- Document schema changes in pull request

## License

This project is proprietary software for QHTI.BG boat marketplace.

## Support

For technical support or questions:
- Create an issue in the project repository
- Contact the development team
- Check the API documentation at `/swagger-ui.html`