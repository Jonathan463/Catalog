-- Initialize the book catalog database
-- This script runs automatically when the PostgreSQL container starts

-- Grant all privileges to the application user
GRANT ALL PRIVILEGES ON DATABASE bookcatalog TO bookcatalog;

-- Optional: Create tables explicitly (JPA will auto-create them, but this serves as documentation)
-- The actual table creation is handled by Spring Boot JPA with ddl-auto: update

