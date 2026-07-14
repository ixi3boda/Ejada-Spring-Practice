# Employee CRUD API (JDBC + Oracle practice project)

A deliberately small Spring Boot service that hits four practice goals:

1. **Solid backend CRUD foundations** - layered architecture (`controller` -> `service` -> `repository`), DTOs separate from the domain model, transactions.
2. **REST APIs** - a proper resource-oriented `/api/v1/employees` endpoint set.
3. **Clean error handling** - a single `GlobalExceptionHandler` (`@RestControllerAdvice`) that turns every exception into one consistent JSON error shape and the right HTTP status.
4. **API documentation** - springdoc-openapi generates a live Swagger UI from the code.
5. **AOP basics** - a `LoggingAspect` that logs every service-layer call, its arguments, execution time, and failures, without a single `log.info(...)` line inside the service class itself.

Persistence is plain **JDBC** (`JdbcTemplate` + hand-written `RowMapper`) against **Oracle** - no JPA/Hibernate - so every SQL statement is explicit and visible in `EmployeeRepositoryImpl`.

## Project layout

```
src/main/java/com/ejada/practice/
 ├─ PracticeApplication.java
 ├─ model/Employee.java                 domain object, mapped by hand from ResultSet
 ├─ dto/                                EmployeeRequest, EmployeeResponse, ApiErrorResponse
 ├─ repository/                         EmployeeRepository (interface) + JdbcTemplate impl + RowMapper
 ├─ service/                            EmployeeService (interface) + impl (business rules, @Transactional)
 ├─ controller/EmployeeController.java  REST endpoints + OpenAPI annotations
 ├─ exception/                          custom exceptions + GlobalExceptionHandler
 ├─ aspect/LoggingAspect.java           @Aspect logging around the service layer
 └─ config/OpenApiConfig.java           Swagger/OpenAPI metadata

src/main/resources/
 ├─ application.properties              datasource + springdoc config
 ├─ schema.sql                          Oracle DDL (drop-if-exists + create table)
 └─ data.sql                            sample seed rows
```

## Prerequisites

- JDK 17+
- Maven (or use the bundled `./mvnw`)
- An Oracle database reachable from your machine (Oracle XE 21c/23ai locally via Docker is the easiest option, or an Autonomous DB / remote instance)

## 1. Point the app at your Oracle database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=practice_user
spring.datasource.password=practice_pw
```

If you don't have Oracle running yet, the quickest way is the official XE container:

```bash
docker run -d --name oracle-xe -p 1521:1521 -e ORACLE_PASSWORD=YourPwd123 gvenzl/oracle-xe:21-slim
```

Then create a user/schema for the app (connect as `system`):

```sql
CREATE USER practice_user IDENTIFIED BY practice_pw;
GRANT CONNECT, RESOURCE, CREATE VIEW TO practice_user;
ALTER USER practice_user QUOTA UNLIMITED ON USERS;
```

`schema.sql` and `data.sql` run automatically on every startup (`spring.sql.init.mode=always`) and (re)create the `employees` table, so you don't need to create it manually.

## 2. Run it

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

## 3. Explore the API docs

- Swagger UI: http://localhost:8080/swagger-ui.html
- Raw OpenAPI JSON: http://localhost:8080/v3/api-docs

## Endpoints

| Method | Path                       | Description              |
|--------|----------------------------|---------------------------|
| GET    | `/api/v1/employees`        | List all employees        |
| GET    | `/api/v1/employees/{id}`   | Get one employee          |
| POST   | `/api/v1/employees`        | Create an employee        |
| PUT    | `/api/v1/employees/{id}`   | Update an employee        |
| DELETE | `/api/v1/employees/{id}`   | Delete an employee        |

Example create request:

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
        "fullName": "Abdelrahman Mostafa",
        "email": "abdelrahman.mostafa@ejada.com",
        "department": "Engineering",
        "salary": 15000.00
      }'
```

## Error handling in practice

Every error, wherever it's thrown, comes back in the same shape:

```json
{
  "timestamp": "2026-07-12T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 42 was not found",
  "path": "/api/v1/employees/42"
}
```

- Unknown id -> `404` (`ResourceNotFoundException`)
- Duplicate email on create/update -> `409` (`DuplicateResourceException`, plus a `DataIntegrityViolationException` fallback if the unique constraint is what actually catches it)
- Failed `@Valid` validation on the request body -> `400`, with a `details` array listing every field error
- Malformed JSON body -> `400`
- Anything unexpected -> `500`, logged with a full stack trace server-side but never leaked to the client

## AOP in practice

`LoggingAspect` defines one pointcut, `execution(* com.ejada.practice.service..*(..))`, that matches every method in the service package, then applies three kinds of advice:

- `@Before` - logs the method and its arguments on entry
- `@Around` - times the call and logs how long it took (or how long it ran before failing)
- `@AfterThrowing` - logs the exception

None of that logging code lives inside `EmployeeServiceImpl` - it's woven in at runtime. Run the app with `logging.level.com.ejada.practice=DEBUG` (already set) and watch the console while you hit the API to see it in action.

## Notes / things to look at next

- `PracticeApplicationTests.contextLoads()` will try to start the full Spring context, which means it needs a real Oracle connection to pass - point it at a test database or add an `@ActiveProfiles("test")` + Testcontainers Oracle setup if you want it to run in CI.
- This is intentionally minimal: no Spring Security, no pagination, no soft deletes. Good next steps once these fundamentals feel solid: add pagination/sorting on `GET /employees`, add a `@ControllerAdvice`-level correlation/request-id, or swap the hand-rolled `RowMapper` wiring for `NamedParameterJdbcTemplate` to practice named parameters.
