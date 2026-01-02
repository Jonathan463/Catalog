# Book Catalog Microservice

A RESTful microservice for managing a catalog of books and authors, built with Spring Boot 3.5.

## Features

- CRUD operations for Authors and Books
- Many-to-many relationship between Books and Authors
- Pagination support for list endpoints
- Request/Response DTO pattern with validation
- Validation for author deletion (cannot delete if books are associated)
- Environment-specific configurations (dev/prod/test)
- Docker support with PostgreSQL

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **PostgreSQL 17** (production) / **H2** (development/test)
- **Lombok** - Reduces boilerplate code
- **Jakarta Bean Validation** - Input validation
- **Maven**
- **Docker & Docker Compose**

## Project Structure

```
src/main/java/com/techforall/bookcatalog/
├── BookCatalogApplication.java     # Main entry point
├── controller/                     # REST Controllers
│   ├── AuthorController.java
│   └── BookController.java
├── service/                        # Business Logic
│   ├── AuthorService.java
│   ├── BookService.java
│   └── impl/
│       ├── AuthorServiceImpl.java
│       └── BookServiceImpl.java
├── repository/                     # Data Access
│   ├── AuthorRepository.java
│   └── BookRepository.java
├── model/                          # Entities & DTOs
│   ├── entity/
│   │   ├── Author.java
│   │   └── Book.java
│   └── dto/
│       ├── request/                # Input DTOs
│       │   ├── AuthorRequest.java
│       │   └── BookRequest.java
│       └── response/               # Output DTOs
│           ├── AuthorResponse.java
│           ├── AuthorSummaryResponse.java
│           ├── BookResponse.java
│           ├── BookSummaryResponse.java
│           └── PageResponse.java
├── exception/                      # Custom Exceptions
│   ├── AuthorHasBooksException.java
│   ├── BadRequestException.java
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
└── utility/                        # Utilities
    └── EntityMapper.java

src/main/resources/
├── application.yml                 # Common configuration
├── application-dev.yml             # Development profile (H2)
├── application-prod.yml            # Production profile (PostgreSQL)
└── logback-spring.xml              # Logging configuration

logs/                               # Log files (auto-created)
├── book-catalog.log                # Main application log
└── book-catalog-error.log          # Error-only log (prod)
```

## Quick Start

### Development Mode (H2 Database)

```bash
# Build and run
./mvnw spring-boot:run

# Or with Maven
mvn spring-boot:run
```

The application runs at `http://localhost:8080`
H2 Console: `http://localhost:8080/h2-console`

### Production Mode (Docker)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## API Endpoints

### Authors

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/authors` | List all authors (paginated) |
| POST | `/authors` | Create new author |
| GET | `/authors/{id}` | Get author details |
| PUT | `/authors/{id}` | Update author |
| DELETE | `/authors/{id}` | Delete author |

### Books

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/books` | List all books (paginated) |
| POST | `/books` | Create new book |
| GET | `/books/{id}` | Get book details |
| PUT | `/books/{id}` | Update book |
| DELETE | `/books/{id}` | Delete book |

### Pagination

List endpoints support pagination with the following query parameters:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | 0 | Page number (zero-based) |
| `size` | 20 | Number of items per page |
| `sort` | - | Sort field and direction (e.g., `title,asc`) |

**Example:** `GET /books?page=0&size=10&sort=title,asc`

## API Examples

See [API_EXAMPLES.md](./docs/API_EXAMPLES.md) for detailed request/response examples.

## Configuration

### Environment Variables (Production)

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | Database host |
| `DB_PORT` | 5432 | Database port |
| `DB_NAME` | bookcatalog | Database name |
| `DB_USERNAME` | bookcatalog | Database username |
| `DB_PASSWORD` | bookcatalog | Database password |
| `LOG_PATH` | logs | Directory for log files |

## Logging

The application uses Logback for logging with profile-specific configurations.

### Log Files

| File | Description |
|------|-------------|
| `logs/book-catalog.log` | Main application log (all levels) |
| `logs/book-catalog-error.log` | Error-only log (production only) |

### Log Levels by Profile

| Profile | Application | Hibernate SQL | Console | File |
|---------|-------------|---------------|---------|------|
| `dev` | DEBUG | DEBUG | ✅ | ✅ |
| `test` | DEBUG | - | ✅ | ❌ |
| `prod` | INFO | WARN | ✅ | ✅ |

### Log Rotation

- **Daily rotation** with additional rotation when file reaches 10MB
- **Compression** - Old logs are gzipped (`.log.gz`)
- **Retention** - 30 days of history, max 1GB total size

### Custom Log Directory

```bash
# Set custom log directory
LOG_PATH=/var/log/bookcatalog mvn spring-boot:run

# Or in Docker
docker run -e LOG_PATH=/app/logs -v /host/logs:/app/logs book-catalog
```

## Business Rules

1. **Author Deletion**: Cannot delete an author if they have associated books (returns HTTP 409 Conflict)
2. **Book Authors**: A book can have zero or more authors
3. **Author Books**: An author can be associated with multiple books

### Create Book - Author Handling

| Case | Input | Result |
|------|-------|--------|
| Empty list | `"authorIds": []` | Create with no authors |
| Single ID invalid | `"authorIds": [99]` | ❌ Throw error, do NOT create |
| Single ID valid | `"authorIds": [12]` | Create with author 12 |
| Multiple IDs, some invalid | `"authorIds": [10, 12]` | Create with valid ones only |
| Multiple IDs, all invalid | `"authorIds": [99, 100]` | ❌ Throw error |

### Update Book - Author Handling

| `authorIds` | Existing Authors? | Result |
|-------------|-------------------|--------|
| `[]` | N/A | Clears authors |
| `[5]` | exists | Updates to [5] |
| `[5]` | missing | ❌ Throw error |
| `[5, 10]` | only 10 exists | Update authors = [10] |
| `[5, 10]` | none exist | ❌ Throw error |

## Validation Rules

### Author
- `name`: Required, max 100 characters
- `surname`: Required, max 100 characters
- `birthYear`: Optional, must be between 1000 and 2100

### Book
- `title`: Required, max 255 characters
- `authorIds`: Optional, can be empty
- `publisher`: Optional, max 255 characters
- `edition`: Optional, max 100 characters
- `publishedDate`: Optional, must be in the past or present

## License

MIT License

