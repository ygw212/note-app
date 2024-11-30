# Notes API Application

A RESTful API service that allows users to create, read, update, and delete notes with sharing capabilities and full-text search functionality.

## Tech Stack & Design Choices

### Core Framework
- **Spring Boot 

### Database
- **PostgreSQL**

### Security
- **Spring Security**
- **JWT (JSON Web Tokens)**

### Third-Party Tools
- **Springdoc OpenAPI (Swagger)**
- **Bucket4j**

## Setup and Installation


### Database Setup

 Configure database connection in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://your_url
spring.datasource.username=noteapp_user
spring.datasource.password=your_password
```

### Application Setup

Update the following properties in `application.properties`:
```properties
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```
Build the application:
```bash
mvn clean install
```
Run the application:
```bash
mvn spring-boot:run
```
Or run the application in the IDE

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/v3/api-docs`



 
