# Weather API

This repository contains a Spring Boot application that exposes a REST API for a simple weather service.  The project is structured as a modular backend with support for:

- **User authentication** (signup, login) with email verification
- **JWT‑based security** and a custom filter to authenticate requests
- **Location management** per user with the ability to mark a default location
- **Email service** for sending verification links via SMTP

Several controllers for weather data and alerts are scaffolds and can be extended in the future.

---

## 🔧 Technology Stack

- Java 17 / Spring Boot
- Spring Security (JWT, password encoding)
- Lombok
- Spring Data JPA / Hibernate
- PostgreSQL (configurable via `application.yml`)
- Spring Mail (Gmail SMTP in samples)
- Maven build

---

## ✅ Features

1. **Signup and login**
   - Users register with username, email and password
   - A verification token is emailed; only verified accounts can log in
   - Authentication response returns a JWT token
2. **JWT Security**
   - Stateless `JwtAuthenticationFilter` parses `Authorization: Bearer ...` headers
   - Secured endpoints require a valid token; `/api/auth/**` is public
3. **User locations**
   - Add a location specifying name, city, coordinates and optional default flag
   - Retrieve all locations for the authenticated user
   - Setting a new default location unsets the previous one
4. **Email service**
   - Simple mail sender using properties from `application.properties`/`application.yml`
5. **Database entities** for `User`, `Location` and `VerificationToken`

> ⚠️ Some packages such as `controller.weather` and `controller.alert` are empty stubs. Feel free to implement your own logic.

---

## 📁 Project Structure

```
weatherAPI/
├─ src/main/java/com/example/weatherAPI/
│  ├─ config/           # security, JWT filter, (empty) swagger/email configs
│  ├─ controller/        # REST controllers by feature
│  ├─ dto/               # request/response objects
│  ├─ entity/            # JPA entities
│  ├─ repository/        # Spring Data repositories
│  ├─ service/           # business logic services
│  └─ util/              # JWT utility, etc.
└─ resources/
   ├─ application.properties
   └─ application.yml     # database, mail and JWT settings
```

---

## 📦 Prerequisites

- Java 17 (or later)
- Maven 3.6+
- PostgreSQL (or any other JDBC‑compatible database)
- Internet access for sending emails (SMTP credentials)

---

## ⚙️ Configuration

Copy `src/main/resources/application.yml` and update the values, or use environment variables / command‑line overrides. Important properties:

```yaml
spring.datasource.url=jdbc:postgresql://localhost:5432/weatherdb
spring.datasource.username=postgres
spring.datasource.password=your-db-password

jwt.secret=your-long-secret-key
jwt.expiration=3600000    # milliseconds

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=you@example.com
spring.mail.password=your-mail-password
```

> **Security note:** never commit real passwords or API keys. Use a secrets manager or environment variables in production.

---

## 🚀 Build & Run

```bash
# from repository root
cd weatherAPI
./mvnw clean package      # build the jar

# run the embedded server
./mvnw spring-boot:run
```

Alternatively, start the generated jar:

```bash
java -jar target/weatherAPI-0.0.1-SNAPSHOT.jar
```

By default the application listens on port `8080`.

---

## 📡 API Endpoints

### Authentication
| Method | URL                | Body / Query                       | Description |
|--------|--------------------|------------------------------------|-------------|
| POST   | `/api/auth/signup` | `username`, `email`, `password`    | Register new user (sends verification email) |
| GET    | `/api/auth/verify` | `?token=<verification-token>`     | Verify email token |
| POST   | `/api/auth/login`  | `email`, `password`               | Authenticate and receive JWT |

### Locations (authenticated)
| Method | URL             | Body                                  | Description |
|--------|-----------------|---------------------------------------|-------------|
| POST   | `/api/locations`| `name`, `city`, `latitude`, `longitude`, `isDefault` | Add location for current user |
| GET    | `/api/locations`| —                                     | List all locations of current user |

`Authorization: Bearer <token>` header required for all `/api/locations` requests.

> Example curl to add location:
> ```bash
> curl -X POST http://localhost:8080/api/locations \
>   -H "Authorization: Bearer $JWT" \
>   -H "Content-Type: application/json" \
>   -d '{"name":"Home","city":"Colombo","latitude":6.9271,"longitude":79.8612,"isDefault":true}'
> ```

### Unimplemented / Future Endpoints
- `/api/weather/**` – placeholder for weather retrieval
- `/api/alerts/**` – placeholder for weather alerts or notifications

---

## 🛠 Development Notes

- Security filter extracts email from JWT then loads application user from the database and places a `UserDetails` with the username into the security context.
- Passwords are stored bcrypt‑hashed; adjust `PasswordEncoder` bean if needed.
- Entities use Lombok for getters/setters; disable in IDE if you run into build issues.
- `spring.jpa.hibernate.ddl-auto=update` is used for development convenience.
- To enable Swagger/OpenAPI add definitions in `SwaggerConfig`.

---

## 📬 Email Setup

The current sample uses Gmail SMTP. To use it:
1. Enable "Less secure app access" or create an app password.
2. Configure credentials in properties or environment variables.
3. The verification link points to `http://localhost:8080/api/auth/verify?token=...` by default.

---

## ✅ Testing

A basic test class `WeatherApiApplicationTests` exists; extend it with additional unit/integration tests.

```bash
./mvnw test
```

---

## 📄 License

This project is open source. Modify and redistribute as you see fit.

---

Feel free to contribute features or raise issues! 😊
