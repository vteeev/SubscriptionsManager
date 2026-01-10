# 📚 Dokumentacja Projektu - Subscription Manager

## 🏗️ Architektura Systemu

Projekt został zbudowany zgodnie z **Clean Architecture** i **Domain-Driven Design (DDD)**, z podziałem na 4 główne warstwy:

```
┌─────────────────────────────────────────┐
│      PRESENTATION (Kontrolery, DTO)     │
├─────────────────────────────────────────┤
│      APPLICATION (Use Cases, DTO)       │
├─────────────────────────────────────────┤
│      DOMAIN (Encje, Value Objects)     │
├─────────────────────────────────────────┤
│   INFRASTRUCTURE (JPA, API, Config)   │
└─────────────────────────────────────────┘
```

---

## 📦 MODUŁ 1: DOMAIN (Warstwa Domenowa)

**Cel**: Zawiera logikę biznesową niezależną od frameworków i technologii.

### 📁 `domain/model/` - Modele domenowe

#### 1. **Subscription.java** - Encja domenowa
- **Rola**: Główna encja reprezentująca subskrypcję użytkownika
- **Zawiera**:
  - `SubscriptionId` - identyfikator subskrypcji
  - `UserId` - identyfikator użytkownika
  - `name` - nazwa subskrypcji (np. "Spotify")
  - `price` - cena (Money)
  - `billingCycle` - cykl rozliczeniowy
  - `nextPaymentDate` - data następnej płatności
  - `autoRenewal` - czy automatyczne odnawianie
  - `status` - status (ACTIVE/CANCELLED)
- **Metody biznesowe**:
  - `create()` - factory method do tworzenia subskrypcji
  - `update()` - aktualizacja szczegółów (z walidacją - nie można aktualizować anulowanej)
  - `cancel()` - anulowanie subskrypcji
  - `isActive()` - sprawdzenie czy aktywna
- **Walidacje**:
  - Nazwa nie może być pusta
  - Data płatności nie może być w przeszłości
  - Nie można aktualizować anulowanej subskrypcji

#### 2. **Money.java** - Value Object
- **Rola**: Reprezentuje pieniądze z walutą (immutable)
- **Zawiera**:
  - `BigDecimal amount` - kwota (precyzja do 2 miejsc po przecinku)
  - `Currency currency` - waluta
- **Metody**:
  - `add(Money)` - dodawanie pieniędzy (tylko tej samej waluty)
  - `multiply(BigDecimal)` - mnożenie kwoty
- **Walidacje**:
  - Kwota nie może być ujemna
  - Automatyczne zaokrąglanie do 2 miejsc po przecinku

#### 3. **SubscriptionId.java** - Value Object
- **Rola**: Identyfikator subskrypcji (UUID wrapper)
- **Metody**:
  - `newId()` - generuje nowy UUID
  - `getValue()` - zwraca UUID
- **Immutable**: Nie można zmienić po utworzeniu

#### 4. **UserId.java** - Value Object
- **Rola**: Identyfikator użytkownika (UUID wrapper)
- **Metody**:
  - `newId()` - generuje nowy UUID
  - `getValue()` - zwraca UUID
- **Immutable**: Nie można zmienić po utworzeniu

#### 5. **BillingCycle.java** - Enum (Strategy Pattern)
- **Rola**: Definiuje cykle rozliczeniowe
- **Wartości**:
  - `MONTHLY(1)` - miesięczny
  - `YEARLY(12)` - roczny
  - `TRIAL(0)` - próbny
- **Metody**:
  - `calculateMonthlyCost(double)` - oblicza miesięczny koszt:
    - TRIAL → 0
    - YEARLY → cena / 12
    - MONTHLY → cena

#### 6. **SubscriptionStatus.java** - Enum
- **Rola**: Status subskrypcji
- **Wartości**:
  - `ACTIVE` - aktywna
  - `CANCELLED` - anulowana

### 📁 `domain/repository/` - Interfejsy repozytoriów

#### 7. **SubscriptionRepository.java** - Interfejs repozytorium
- **Rola**: Definiuje kontrakt dla dostępu do danych subskrypcji
- **Metody**:
  - `save(Subscription)` - zapisuje subskrypcję
  - `findById(SubscriptionId)` - znajdź po ID
  - `findByUserId(UserId)` - znajdź wszystkie dla użytkownika
  - `findActiveByUserId(UserId)` - znajdź aktywne dla użytkownika
  - `delete(SubscriptionId)` - usuwa subskrypcję
  - `existsById(SubscriptionId)` - sprawdza istnienie
- **Zasada**: Domena definiuje interfejs, infrastruktura implementuje

### 📁 `domain/exchange/` - Interfejsy zewnętrzne

#### 8. **ExchangeRateProvider.java** - Interfejs dostawcy kursów
- **Rola**: Abstrakcja dla konwersji walut (Dependency Inversion)
- **Metody**:
  - `getExchangeRate(Currency, Currency)` - pobiera kurs wymiany
  - `convert(Money, Currency)` - konwertuje pieniądze (default method)
- **Zasada**: Domena nie wie o NBP, tylko o interfejsie

### 📁 `domain/service/` - Serwisy domenowe

#### 9. **BillingService.java** - Serwis domenowy
- **Rola**: Obsługuje złożoną logikę biznesową związaną z rozliczeniami
- **Zawiera**:
  - `ExchangeRateProvider` - do konwersji walut
  - `Currency baseCurrency` - waluta bazowa (PLN)
- **Metody**:
  - `calculateMonthlyCost(List<Subscription>)` - oblicza łączny miesięczny koszt:
    1. Filtruje tylko aktywne subskrypcje
    2. Dla każdej oblicza miesięczny koszt (uwzględniając cykl)
    3. Konwertuje wszystkie do waluty bazowej
    4. Sumuje wszystkie kwoty
    5. Zwraca `Money` w walucie bazowej

---

## 📦 MODUŁ 2: APPLICATION (Warstwa Aplikacyjna)

**Cel**: Realizuje przypadki użycia, koordynuje logikę domenową.

### 📁 `application/usecase/` - Przypadki użycia

#### 1. **AddSubscriptionUseCase.java**
- **Rola**: Dodaje nową subskrypcję
- **Zależności**: `SubscriptionRepository`
- **Proces**:
  1. Przyjmuje `CreateSubscriptionCommand`
  2. Tworzy `SubscriptionId` (nowy UUID)
  3. Konwertuje dane wejściowe na obiekty domenowe:
     - String → `UserId`
     - Double + String → `Money`
     - String → `BillingCycle`
  4. Tworzy encję `Subscription` przez factory method
  5. Zapisuje przez repository
  6. Konwertuje do `SubscriptionDto` i zwraca

#### 2. **CancelSubscriptionUseCase.java**
- **Rola**: Anuluje subskrypcję
- **Zależności**: `SubscriptionRepository`
- **Proces**:
  1. Przyjmuje `subscriptionId` (String)
  2. Konwertuje na `SubscriptionId`
  3. Pobiera subskrypcję z repository
  4. Wywołuje `subscription.cancel()` (logika domenowa)
  5. Zapisuje zmiany

#### 3. **ListSubscriptionsUseCase.java**
- **Rola**: Listuje subskrypcje użytkownika
- **Zależności**: `SubscriptionRepository`
- **Metody**:
  - `execute(String userId)` - wszystkie subskrypcje
  - `executeActive(String userId)` - tylko aktywne
- **Proces**:
  1. Konwertuje String → `UserId`
  2. Pobiera subskrypcje z repository
  3. Mapuje na `SubscriptionDto` i zwraca listę

#### 4. **CalculateMonthlyCostUseCase.java**
- **Rola**: Oblicza miesięczny koszt wszystkich subskrypcji
- **Zależności**: 
  - `SubscriptionRepository`
  - `ExchangeRateProvider`
  - `Currency baseCurrency`
- **Proces**:
  1. Pobiera aktywne subskrypcje użytkownika
  2. Tworzy `BillingService` z providerem i walutą bazową
  3. Wywołuje `billingService.calculateMonthlyCost()`
  4. Konwertuje `Money` → `MonthlyCostDto`

### 📁 `application/dto/` - Obiekty transferu danych

#### 5. **CreateSubscriptionCommand.java** (Record)
- **Rola**: DTO dla tworzenia subskrypcji
- **Pola**: `userId`, `name`, `price`, `currency`, `billingCycle`, `nextPaymentDate`, `autoRenewal`

#### 6. **SubscriptionDto.java** (Record)
- **Rola**: DTO reprezentujące subskrypcję
- **Pola**: wszystkie pola subskrypcji jako proste typy

#### 7. **MonthlyCostDto.java** (Record)
- **Rola**: DTO dla miesięcznego kosztu
- **Pola**: `amount` (Double), `currency` (String)

---

## 📦 MODUŁ 3: INFRASTRUCTURE (Warstwa Infrastruktury)

**Cel**: Implementuje techniczne szczegóły (baza danych, API zewnętrzne, konfiguracja).

### 📁 `infrastructure/persistence/jpa/` - Persystencja

#### 1. **SubscriptionEntity.java** - Encja JPA
- **Rola**: Mapuje domenową `Subscription` na tabelę bazy danych
- **Tabela**: `subscriptions`
- **Pola**:
  - `id` (UUID) - PRIMARY KEY
  - `user_id` (UUID) - NOT NULL
  - `name` (VARCHAR) - NOT NULL
  - `price_amount` (DECIMAL) - NOT NULL
  - `price_currency` (VARCHAR(3)) - NOT NULL
  - `billing_cycle` (VARCHAR) - NOT NULL (ENUM)
  - `next_payment_date` (DATE) - NOT NULL
  - `auto_renewal` (BOOLEAN) - NOT NULL
  - `status` (VARCHAR) - NOT NULL (ENUM)
- **Enumeracje**: `BillingCycleEnum`, `SubscriptionStatusEnum` (dla JPA)

#### 2. **SpringDataSubscriptionRepository.java** - Spring Data JPA
- **Rola**: Interfejs Spring Data dla operacji na bazie
- **Rozszerza**: `JpaRepository<SubscriptionEntity, UUID>`
- **Metody**:
  - `findByUserId(UUID)` - znajdź po user_id
  - `findByUserIdAndStatus(UUID, Status)` - znajdź po user_id i statusie

#### 3. **JpaSubscriptionRepository.java** - Adapter
- **Rola**: Implementuje `SubscriptionRepository` z domeny używając JPA
- **Zawiera**: `SpringDataSubscriptionRepository`
- **Metody mapowania**:
  - `toEntity(Subscription)` - domena → encja JPA
  - `toDomain(SubscriptionEntity)` - encja JPA → domena
- **Proces**:
  - `save()`: domena → encja → zapis → encja → domena
  - `findById()`: UUID → encja → domena
  - `findByUserId()`: UserId → UUID → lista encji → lista domen

### 📁 `infrastructure/exchange/` - Integracje zewnętrzne

#### 4. **NbpExchangeRateProvider.java** - Implementacja ExchangeRateProvider
- **Rola**: Pobiera kursy walut z API NBP (Narodowy Bank Polski)
- **API**: `http://api.nbp.pl/api/exchangerates/rates/a/{currency}/?format=json`
- **Zawiera**: `RestTemplate` do HTTP requests
- **Logika konwersji**:
  - Ta sama waluta → 1.0
  - PLN → inna: pobiera kurs z NBP, odwraca (1/rate)
  - Inna → PLN: pobiera kurs z NBP
  - Inna → inna: przez PLN (rate1/rate2)
- **Obsługa błędów**: Zwraca `Optional.empty()` przy błędzie

### 📁 `infrastructure/config/` - Konfiguracja

#### 5. **ApplicationConfig.java** - Konfiguracja Use Cases
- **Rola**: Tworzy bean'y dla wszystkich Use Cases
- **Beany**:
  - `AddSubscriptionUseCase`
  - `CancelSubscriptionUseCase`
  - `ListSubscriptionsUseCase`
  - `CalculateMonthlyCostUseCase` (z `ExchangeRateProvider` i `Currency`)

#### 6. **ExchangeRateConfig.java** - Konfiguracja Exchange Rate
- **Rola**: Tworzy `RestTemplate` bean dla HTTP requests do NBP

---

## 📦 MODUŁ 4: PRESENTATION (Warstwa Prezentacji)

**Cel**: Komunikacja z użytkownikiem przez REST API.

### 📁 `presentation/controller/` - Kontrolery REST

#### 1. **SubscriptionController.java** - REST Controller
- **Rola**: Obsługuje HTTP requests dla subskrypcji
- **Base path**: `/api/subscriptions`
- **Zawiera**: Wszystkie Use Cases i `SubscriptionMapper`

**Endpointy**:

1. **POST `/api/subscriptions`**
   - **Rola**: Tworzy nową subskrypcję
   - **Request Body**: `CreateSubscriptionRequest` (JSON)
   - **Response**: `SubscriptionResponse` (201 Created)
   - **Proces**:
     - Walidacja przez `@Valid`
     - Mapowanie request → command
     - Wywołanie `AddSubscriptionUseCase`
     - Mapowanie DTO → response

2. **GET `/api/subscriptions/user/{userId}`**
   - **Rola**: Listuje wszystkie subskrypcje użytkownika
   - **Path Variable**: `userId` (String UUID)
   - **Response**: Lista `SubscriptionResponse` (200 OK)
   - **Proces**:
     - Wywołanie `ListSubscriptionsUseCase.execute(userId)`
     - Mapowanie listy DTO → lista response

3. **GET `/api/subscriptions/user/{userId}/active`**
   - **Rola**: Listuje tylko aktywne subskrypcje
   - **Path Variable**: `userId` (String UUID)
   - **Response**: Lista `SubscriptionResponse` (200 OK)
   - **Proces**: Jak wyżej, ale `executeActive()`

4. **DELETE `/api/subscriptions/{subscriptionId}`**
   - **Rola**: Anuluje subskrypcję
   - **Path Variable**: `subscriptionId` (String UUID)
   - **Response**: 204 No Content
   - **Proces**:
     - Wywołanie `CancelSubscriptionUseCase.execute(subscriptionId)`

5. **GET `/api/subscriptions/user/{userId}/cost/monthly`**
   - **Rola**: Oblicza miesięczny koszt wszystkich aktywnych subskrypcji
   - **Path Variable**: `userId` (String UUID)
   - **Response**: `MonthlyCostResponse` (200 OK)
   - **Proces**:
     - Wywołanie `CalculateMonthlyCostUseCase.execute(userId)`
     - Zwraca kwotę w walucie bazowej (PLN)

### 📁 `presentation/mapper/` - Mapowanie

#### 2. **SubscriptionMapper.java** - Mapper
- **Rola**: Konwertuje między warstwą prezentacji a aplikacyjną
- **Metody**:
  - `toCommand(CreateSubscriptionRequest)` → `CreateSubscriptionCommand`
  - `toResponse(SubscriptionDto)` → `SubscriptionResponse`
  - `toResponseList(List<SubscriptionDto>)` → `List<SubscriptionResponse>`
  - `toResponse(MonthlyCostDto)` → `MonthlyCostResponse`
- **Zawiera**: Definicje Request/Response DTO jako record'y

---

## 🗄️ BAZA DANYCH

### Typ bazy danych
- **H2 Database** (in-memory)
- **Nie jest w Dockerze** - działa w pamięci JVM
- **Konfiguracja**: `application.properties`
  - URL: `jdbc:h2:mem:subscriptiondb`
  - Username: `sa`
  - Password: (puste)

### Migracje (Flyway)
- **Lokalizacja**: `src/main/resources/db/migration/`
- **V1__create_subscriptions_table.sql**:
  - Tworzy tabelę `subscriptions`
  - Indeksy: `user_id`, `user_id + status`, `status`
  - Kolumny: `id`, `user_id`, `name`, `price_amount`, `price_currency`, `billing_cycle`, `next_payment_date`, `auto_renewal`, `status`, `created_at`, `updated_at`

### H2 Console
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:subscriptiondb`
- **Dostęp**: Włączony w konfiguracji

---

## 🚀 URUCHOMIENIE SERWERA I DZIAŁANIE

### Co się dzieje po uruchomieniu?

1. **Start aplikacji Spring Boot**:
   ```bash
   gradlew.bat bootRun
   ```

2. **Inicjalizacja Spring Context**:
   - Skanowanie komponentów (@Component, @Service, @Repository)
   - Tworzenie bean'ów (Use Cases, Repositories, Config)
   - Konfiguracja JPA/Hibernate
   - Uruchomienie Flyway migracji

3. **Flyway wykonuje migracje**:
   - Wykonuje `V1__create_subscriptions_table.sql`
   - Tworzy tabelę `subscriptions` w H2

4. **H2 Database startuje**:
   - Baza w pamięci (dane znikną po restarcie)

5. **Tomcat Embedded Server startuje**:
   - Port: `8080`
   - Context path: `/`

6. **Spring Security aktywuje się**:
   - **UWAGA**: Spring Security jest włączony (dependency w build.gradle)
   - Domyślnie zabezpiecza wszystkie endpointy

### Co się dzieje po wejściu na `http://localhost:8080`?

**Odpowiedź**: **Spring Security pokazuje formularz logowania**

**Dlaczego?**
- `spring-boot-starter-security` jest w zależnościach
- Brak konfiguracji Security (SecurityConfig został usunięty)
- Spring Security domyślnie:
  - Wymaga autentykacji dla wszystkich endpointów
  - Pokazuje formularz logowania HTML
  - Domyślny użytkownik: `user`
  - Hasło: generowane losowo (w logach)

**Jak uzyskać dostęp do API?**

**Opcja 1**: Wyłączyć Security (tymczasowo)
- Usunąć `spring-boot-starter-security` z `build.gradle`

**Opcja 2**: Skonfigurować Security (zalecane)
- Dodać `SecurityConfig` pozwalający na dostęp do `/api/**`

**Opcja 3**: Użyć domyślnego użytkownika
- Sprawdzić hasło w logach (szukać "Using generated security password")
- Użyć Basic Auth w requestach

### Przykładowe requesty (bez Security):

```bash
# 1. Dodaj subskrypcję
POST http://localhost:8080/api/subscriptions
Content-Type: application/json

{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Spotify",
  "price": 29.99,
  "currency": "PLN",
  "billingCycle": "MONTHLY",
  "nextPaymentDate": "2024-02-01",
  "autoRenewal": true
}

# 2. Lista subskrypcji
GET http://localhost:8080/api/subscriptions/user/550e8400-e29b-41d4-a716-446655440000

# 3. Miesięczny koszt
GET http://localhost:8080/api/subscriptions/user/550e8400-e29b-41d4-a716-446655440000/cost/monthly
```

---

## 🔄 PRZEPŁYW DANYCH - Przykład: Dodanie subskrypcji

1. **HTTP Request** → `POST /api/subscriptions`
2. **SubscriptionController** → odbiera request
3. **SubscriptionMapper** → konwertuje JSON → `CreateSubscriptionCommand`
4. **AddSubscriptionUseCase** → wykonuje logikę:
   - Tworzy `SubscriptionId` (UUID)
   - Konwertuje dane → obiekty domenowe
   - Wywołuje `Subscription.create()` (factory method)
5. **SubscriptionRepository** (interfejs) → wywołanie `save()`
6. **JpaSubscriptionRepository** (implementacja) → mapuje domenę → encję JPA
7. **SpringDataSubscriptionRepository** → zapis do H2 przez JPA
8. **H2 Database** → INSERT INTO subscriptions
9. **Odwrotny przepływ**: Encja → Domena → DTO → Response → JSON

---

## 📊 PODSUMOWANIE ARCHITEKTURY

| Warstwa | Zależności | Niezależność |
|---------|-----------|--------------|
| **Domain** | Brak (tylko Java) | ✅ Nie zna Spring, JPA, HTTP |
| **Application** | Domain | ✅ Nie zna HTTP, JPA |
| **Infrastructure** | Domain, Application | ✅ Implementuje interfejsy z Domain |
| **Presentation** | Application | ✅ Nie zna Domain bezpośrednio |

**Zasada**: Zależności skierowane do wewnątrz (do Domain). Domain jest najważniejsza i niezależna.

---

## 🎯 ENDPOINTY - Pełna lista

| Metoda | Endpoint | Opis | Request | Response |
|--------|----------|------|---------|----------|
| POST | `/api/subscriptions` | Dodaj subskrypcję | `CreateSubscriptionRequest` | `SubscriptionResponse` (201) |
| GET | `/api/subscriptions/user/{userId}` | Lista subskrypcji | - | `List<SubscriptionResponse>` (200) |
| GET | `/api/subscriptions/user/{userId}/active` | Lista aktywnych | - | `List<SubscriptionResponse>` (200) |
| DELETE | `/api/subscriptions/{subscriptionId}` | Anuluj subskrypcję | - | 204 No Content |
| GET | `/api/subscriptions/user/{userId}/cost/monthly` | Miesięczny koszt | - | `MonthlyCostResponse` (200) |

**Uwaga**: Wszystkie endpointy wymagają autentykacji (Spring Security domyślnie).

---

## 🔍 DIAGNOSTYKA

### Sprawdzenie czy aplikacja działa:
1. Logi Spring Boot - szukaj "Started SubscriptionManagerApplication"
2. H2 Console - `http://localhost:8080/h2-console`
3. Health check - `http://localhost:8080/actuator/health` (jeśli actuator włączony)

### Problemy z Security:
- Sprawdź logi: "Using generated security password"
- Użyj tego hasła w Basic Auth
- Lub wyłącz Security tymczasowo

---

**Dokumentacja utworzona**: 2024-01-09
**Wersja projektu**: 0.0.1-SNAPSHOT
