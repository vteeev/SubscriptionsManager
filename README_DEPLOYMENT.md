# 🚀 Instrukcja Deployment - Subscription Manager

## 📋 Wymagania

- Docker i Docker Compose
- Java 25 (lub zgodna wersja)
- Gradle

## 🗄️ Baza danych PostgreSQL

### Opcja 1: Docker Compose (Zalecane)

1. **Uruchom PostgreSQL w Dockerze:**
   ```bash
   docker-compose up -d
   ```

2. **Sprawdź czy kontener działa:**
   ```bash
   docker ps
   ```

3. **Zatrzymaj bazę danych:**
   ```bash
   docker-compose down
   ```

### Opcja 2: Lokalna instalacja PostgreSQL

1. Zainstaluj PostgreSQL lokalnie
2. Utwórz bazę danych:
   ```sql
   CREATE DATABASE subscriptiondb;
   CREATE USER subscription_user WITH PASSWORD 'subscription_pass';
   GRANT ALL PRIVILEGES ON DATABASE subscriptiondb TO subscription_user;
   ```

## ⚙️ Konfiguracja aplikacji

### Plik `application.properties`

Aplikacja jest skonfigurowana do pracy z PostgreSQL:
- **URL**: `jdbc:postgresql://localhost:5432/subscriptiondb`
- **User**: `subscription_user`
- **Password**: `subscription_pass`

### Zmienne środowiskowe (opcjonalnie)

Możesz nadpisać konfigurację przez zmienne środowiskowe:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/subscriptiondb
export SPRING_DATASOURCE_USERNAME=subscription_user
export SPRING_DATASOURCE_PASSWORD=subscription_pass
```

## 🏃 Uruchomienie aplikacji

1. **Upewnij się, że PostgreSQL działa:**
   ```bash
   docker-compose up -d
   ```

2. **Uruchom aplikację:**
   ```bash
   ./gradlew bootRun
   ```
   lub na Windows:
   ```bash
   gradlew.bat bootRun
   ```

3. **Otwórz przeglądarkę:**
   ```
   http://localhost:8080
   ```

## 📝 Migracje Flyway

Flyway automatycznie wykona migracje przy starcie aplikacji:
- `V1__create_subscriptions_table.sql` - tworzy tabelę subskrypcji
- `V2__create_users_table.sql` - tworzy tabelę użytkowników

## 🌐 Frontend

Frontend jest dostępny pod adresem `http://localhost:8080`:
- **Logowanie/Rejestracja** - na stronie głównej
- **Zarządzanie subskrypcjami** - po zalogowaniu
- **API REST** - dostępne pod `/api/*`

## 🔐 Bezpieczeństwo

- **JWT Tokens** - autentykacja przez tokeny JWT
- **BCrypt** - hasła są hashowane
- **CORS** - skonfigurowany dla frontendu

## 📊 Endpointy API

Wszystkie endpointy wymagają autentykacji (JWT token w headerze `Authorization: Bearer <token>`):

- `POST /api/auth/register` - rejestracja
- `POST /api/auth/login` - logowanie
- `POST /api/subscriptions` - dodaj subskrypcję
- `GET /api/subscriptions` - lista subskrypcji
- `GET /api/subscriptions/active` - lista aktywnych
- `DELETE /api/subscriptions/{id}` - anuluj subskrypcję
- `GET /api/subscriptions/cost/monthly` - miesięczny koszt

## 🐳 Deployment w Dockerze (Opcjonalne)

Możesz również uruchomić całą aplikację w Dockerze:

1. Utwórz `Dockerfile`:
```dockerfile
FROM openjdk:25-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

2. Zbuduj obraz:
```bash
./gradlew build
docker build -t subscription-manager .
```

3. Uruchom z docker-compose (dodaj do docker-compose.yml):
```yaml
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/subscriptiondb
```

## 🔧 Troubleshooting

### Problem: Nie można połączyć się z bazą danych

**Rozwiązanie:**
- Sprawdź czy PostgreSQL działa: `docker ps`
- Sprawdź logi: `docker-compose logs postgres`
- Sprawdź port 5432: `netstat -an | grep 5432`

### Problem: Migracje nie wykonują się

**Rozwiązanie:**
- Sprawdź logi aplikacji
- Sprawdź czy użytkownik ma uprawnienia do tworzenia tabel
- Sprawdź czy Flyway jest włączony w `application.properties`

### Problem: Frontend nie ładuje się

**Rozwiązanie:**
- Sprawdź czy pliki są w `src/main/resources/static/`
- Sprawdź konfigurację Security (powinna pozwalać na `/`, `/css/**`, `/js/**`)
- Sprawdź logi aplikacji

## 📚 Dodatkowe informacje

- **H2 Console** - wyłączona (używamy PostgreSQL)
- **Port aplikacji** - 8080
- **Port PostgreSQL** - 5432
- **Baza danych** - `subscriptiondb`
