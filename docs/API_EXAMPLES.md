# API Examples

## Authors

### Create Author

**Request:**
```bash
POST http://localhost:8080/authors
Content-Type: application/json

{
    "name": "George",
    "surname": "Orwell",
    "birthYear": 1903
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "name": "George",
    "surname": "Orwell",
    "birthYear": 1903,
    "books": []
}
```

### List All Authors (Paginated)

**Request:**
```bash
GET http://localhost:8080/authors?page=0&size=20
```

**Response (200 OK):**
```json
{
    "content": [
        {
            "id": 1,
            "fullName": "George Orwell"
        },
        {
            "id": 2,
            "fullName": "Aldous Huxley"
        }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
}
```

### Get Author by ID

**Request:**
```bash
GET http://localhost:8080/authors/1
```

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "George",
    "surname": "Orwell",
    "birthYear": 1903,
    "books": [
        {
            "id": 1,
            "title": "1984",
            "publisher": "Secker & Warburg"
        }
    ]
}
```

### Update Author

**Request:**
```bash
PUT http://localhost:8080/authors/1
Content-Type: application/json

{
    "name": "George",
    "surname": "Orwell",
    "birthYear": 1903
}
```

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "George",
    "surname": "Orwell",
    "birthYear": 1903,
    "books": [
        {
            "id": 1,
            "title": "1984",
            "publisher": "Secker & Warburg"
        }
    ]
}
```

### Delete Author

**Request:**
```bash
DELETE http://localhost:8080/authors/1
```

**Response (204 No Content)**

**Error Response (409 Conflict) - When author has books:**
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 409,
    "error": "Conflict",
    "message": "Cannot delete author with id '1' because they have associated books",
    "path": "/authors/1"
}
```

## Books

### Create Book

**Request:**
```bash
POST http://localhost:8080/books
Content-Type: application/json

{
    "title": "1984",
    "authorIds": [1],
    "publisher": "Secker & Warburg",
    "edition": "First Edition",
    "publishedDate": "1949-06-08"
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "title": "1984",
    "authors": [
        {
            "id": 1,
            "fullName": "George Orwell"
        }
    ],
    "publisher": "Secker & Warburg",
    "edition": "First Edition",
    "publishedDate": "1949-06-08"
}
```

### List All Books (Paginated)

**Request:**
```bash
GET http://localhost:8080/books?page=0&size=20&sort=title,asc
```

**Response (200 OK):**
```json
{
    "content": [
        {
            "id": 1,
            "title": "1984",
            "publisher": "Secker & Warburg"
        },
        {
            "id": 2,
            "title": "Brave New World",
            "publisher": "Chatto & Windus"
        }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
}
```

### Get Book by ID

**Request:**
```bash
GET http://localhost:8080/books/1
```

**Response (200 OK):**
```json
{
    "id": 1,
    "title": "1984",
    "authors": [
        {
            "id": 1,
            "fullName": "George Orwell"
        }
    ],
    "publisher": "Secker & Warburg",
    "edition": "First Edition",
    "publishedDate": "1949-06-08"
}
```

### Update Book

**Request:**
```bash
PUT http://localhost:8080/books/1
Content-Type: application/json

{
    "title": "Nineteen Eighty-Four",
    "authorIds": [1],
    "publisher": "Secker & Warburg",
    "edition": "Revised Edition",
    "publishedDate": "1949-06-08"
}
```

**Response (200 OK):**
```json
{
    "id": 1,
    "title": "Nineteen Eighty-Four",
    "authors": [
        {
            "id": 1,
            "fullName": "George Orwell"
        }
    ],
    "publisher": "Secker & Warburg",
    "edition": "Revised Edition",
    "publishedDate": "1949-06-08"
}
```

### Delete Book

**Request:**
```bash
DELETE http://localhost:8080/books/1
```

**Response (204 No Content)**

## Error Responses

### Resource Not Found (404)

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "Author not found with id: '999'",
    "path": "/authors/999"
}
```

### Validation Error (400)

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/authors",
    "fieldErrors": [
        {
            "field": "name",
            "message": "Name is required"
        }
    ]
}
```

### Book Without Authors (400)

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/books",
    "fieldErrors": [
        {
            "field": "authorIds",
            "message": "At least one author is required"
        }
    ]
}
```

## Logging

Application logs are written to `logs/book-catalog.log`. Example log entries:

```
2025-12-02 10:30:00.123 INFO  [http-nio-8080-exec-1] c.d.b.controller.BookController - POST /books - Creating new book: 1984
2025-12-02 10:30:00.125 DEBUG [http-nio-8080-exec-1] c.d.b.service.impl.BookServiceImpl - Creating new book: 1984
2025-12-02 10:30:00.150 INFO  [http-nio-8080-exec-1] c.d.b.service.impl.BookServiceImpl - Created book with id: 1
```

### Viewing Logs

```bash
# Follow live logs
tail -f logs/book-catalog.log

# View errors only (production)
tail -f logs/book-catalog-error.log

# Search for specific requests
grep "POST /books" logs/book-catalog.log
```

