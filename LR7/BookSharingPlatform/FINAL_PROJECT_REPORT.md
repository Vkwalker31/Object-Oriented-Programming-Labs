# FINAL PROJECT REPORT  
**Проект:** Book Exchange Platform  
**Базовый пакет:** `com.example.bookexchange`  
**Технологии:** Java 17, Spring Boot 3.2, Spring Web, Spring Security, Spring Data JPA, Validation, PostgreSQL, H2, Flyway, SpringDoc OpenAPI, JUnit 5, Mockito, Maven.

---

## 1. Что реализовано и для чего

### 1.1 Пользователи (Auth)
- `POST /api/auth/register` — регистрация пользователя (`username`, `email`, `password`).
- `POST /api/auth/login` — логин и выдача JWT.
- Для паролей используется `BCryptPasswordEncoder`.
- Для авторизации используется JWT (`Authorization: Bearer <token>`).

**Зачем:** обеспечивает безопасную идентификацию пользователей и доступ к защищенным операциям.

### 1.2 Каталог книг
- `POST /api/books` — добавление книги (`title`, `author`, `isbn`).
- `GET /api/books` — поиск книг по фильтрам (`title`, `author`, `isbn`).
- `GET /api/books/{bookId}` — получение книги по ID.

**Зачем:** централизованный каталог для обмена книгами и фильтрации.

### 1.3 Личный фонд (Inventory)
- `POST /api/inventory` — добавить книгу в личный фонд пользователя.
- `GET /api/inventory/me` — получить свой фонд.
- `PATCH /api/inventory/{inventoryId}/status` — смена статуса.
- Статусы инвентаря: `AVAILABLE`, `BUSY`.

**Зачем:** отражает владение конкретной копией книги и ее доступность к обмену.

### 1.4 Обмен книгами (Exchange)
- `POST /api/exchanges` — запрос на обмен у владельца.
- `GET /api/exchanges/incoming` — входящие запросы.
- `GET /api/exchanges/outgoing` — исходящие запросы.
- `PATCH /api/exchanges/{exchangeId}/status` — смена статуса обмена.
- Статусы обмена: `REQUESTED -> APPROVED -> TRANSFERRED -> COMPLETED`.
- `GET /api/exchanges/history` — история движений книги.

**Зачем:** реализует полный жизненный цикл обмена и отслеживание перемещений.

### 1.5 Рейтинг и отзывы
- `POST /api/reviews` — оставить отзыв после успешного обмена.
- `GET /api/reviews?targetType=USER|BOOK&targetId=...` — получить отзывы по цели.
- Поддержаны отзывы:
  - для пользователя (`targetType=USER`);
  - для книги (`targetType=BOOK`).
- Рейтинг пересчитывается автоматически:
  - `User.rating`;
  - `Book.rating`.

**Зачем:** повышает доверие в системе и качество каталога.

---

## 2. Подтверждение соблюдения Clean Architecture

### 2.1 Слои
- `models` — чистые доменные модели (без JPA/Spring/Web аннотаций).
- `controllers` — use-cases с бизнес-логикой.
- `controllers.port` — абстракции доступа (DIP).
- `clients` — инфраструктура: JPA entities, Spring Data repositories, adapters.
- `api` — REST контроллеры + web DTO + Swagger аннотации.
- `shared` — общие exception/security/config/response классы.

### 2.2 Правило зависимостей
- Use-cases (`controllers`) зависят только от `controllers.port`, не от `clients.adapter`.
- `clients` реализует порты и зависит от фреймворков.
- `api` оркестрирует HTTP и вызывает use-cases.
- Внутренние слои не знают про HTTP/JPA.

### 2.3 Технические подтверждения
- JWT filter + `SecurityConfig` реализованы.
- `GlobalExceptionHandler` реализован, формат ошибок унифицирован через `ApiResponse`.
- Swagger/OpenAPI присутствует (`@Tag`, `@Operation`, `OpenApiConfig`).
- Unit-тесты use-cases есть (JUnit5 + Mockito), включая проверку недоступного статуса `BUSY`.

---

## 3. Подтверждение выполнения требований задания

| Требование задания | Статус |
|---|---|
| Реализован backend платформы обмена книгами | ✅ |
| Регистрация/аутентификация/авторизация | ✅ |
| Каталог (добавление + фильтры) | ✅ |
| Владение (личный фонд + статус) | ✅ |
| Обмен (workflow + история) | ✅ |
| Рейтинг/отзывы | ✅ (для USER и BOOK) |
| Swagger UI как пользовательский интерфейс | ✅ |
| Архитектура в стиле Clean Architecture | ✅ |
| Написаны тесты use-case слоя | ✅ |
| Создана проектная документация | ✅ |

---

## 4. Чеклист smoke-тестирования через Swagger (для защиты)

Swagger: `http://localhost:8080/api/swagger-ui.html`

1. **Auth /register**  
   Зарегистрировать user1 и user2.
2. **Auth /login**  
   Логин user1, сохранить JWT.
3. **Authorize**  
   В Swagger нажать Authorize и вставить `Bearer <jwt_user1>`.
4. **Books POST**  
   Создать книгу.
5. **Inventory POST**  
   Добавить книгу в фонд user1.
6. **Auth /login user2 + Authorize**  
   Логин user2, подставить его JWT.
7. **Exchanges POST**  
   Создать запрос на `inventoryId` user1.
8. **Exchanges /incoming user1**  
   Под user1 увидеть входящий запрос.
9. **Exchanges PATCH status**  
   Проставить `APPROVED`, затем `TRANSFERRED`, затем `COMPLETED`.
10. **Exchanges /history**  
    Проверить историю перемещений.
11. **Reviews POST (USER)**  
    Под user2 оставить отзыв владельцу.
12. **Reviews POST (BOOK)**  
    Под user2 оставить отзыв книге.
13. **Reviews GET**  
    Получить отзывы отдельно по user и по book.

Ожидаемо:
- нельзя запросить обмен для `BUSY`;
- после запроса инвентарь становится `BUSY`;
- после `COMPLETED` инвентарь снова `AVAILABLE`;
- отзывы доступны только после `TRANSFERRED/COMPLETED`.

---

## 5. Готовый e2e сценарий curl (двое пользователей, обмен, история, USER+BOOK review)

> Ниже Unix-style (`bash`). Для PowerShell можно выполнять те же URL и JSON через `Invoke-RestMethod`.

### 5.1 Register user1
```bash
curl -s -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"user1@mail.com","password":"password123"}'
```

### 5.2 Register user2
```bash
curl -s -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"user2","email":"user2@mail.com","password":"password123"}'
```

### 5.3 Login user1
```bash
TOKEN1=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@mail.com","password":"password123"}' | jq -r '.data.accessToken')
echo $TOKEN1
```

### 5.4 Create book (user1)
```bash
BOOK_ID=$(curl -s -X POST "http://localhost:8080/api/books" \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Architecture","author":"Robert C. Martin","isbn":"9780134494166"}' | jq -r '.data.id')
echo $BOOK_ID
```

### 5.5 Add to inventory (user1)
```bash
INVENTORY_ID=$(curl -s -X POST "http://localhost:8080/api/inventory" \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d "{\"bookId\":\"$BOOK_ID\",\"condition\":\"GOOD\"}" | jq -r '.data.id')
echo $INVENTORY_ID
```

### 5.6 Login user2
```bash
TOKEN2=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"user2@mail.com","password":"password123"}' | jq -r '.data.accessToken')
echo $TOKEN2
```

### 5.7 Create exchange request (user2 -> user1 inventory)
```bash
EXCHANGE_ID=$(curl -s -X POST "http://localhost:8080/api/exchanges" \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Content-Type: application/json" \
  -d "{\"inventoryId\":\"$INVENTORY_ID\"}" | jq -r '.data.id')
echo $EXCHANGE_ID
```

### 5.8 Update statuses as owner (user1)
```bash
curl -s -X PATCH "http://localhost:8080/api/exchanges/$EXCHANGE_ID/status" \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"status":"APPROVED"}'

curl -s -X PATCH "http://localhost:8080/api/exchanges/$EXCHANGE_ID/status" \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"status":"TRANSFERRED"}'

curl -s -X PATCH "http://localhost:8080/api/exchanges/$EXCHANGE_ID/status" \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"status":"COMPLETED"}'
```

### 5.9 History
```bash
curl -s "http://localhost:8080/api/exchanges/history" \
  -H "Authorization: Bearer $TOKEN1"
```

### 5.10 Review USER (user2 -> user1)
```bash
curl -s -X POST "http://localhost:8080/api/reviews" \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Content-Type: application/json" \
  -d "{\"exchangeId\":\"$EXCHANGE_ID\",\"targetType\":\"USER\",\"rating\":5,\"comment\":\"Reliable owner\"}"
```

### 5.11 Review BOOK
```bash
curl -s -X POST "http://localhost:8080/api/reviews" \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Content-Type: application/json" \
  -d "{\"exchangeId\":\"$EXCHANGE_ID\",\"targetType\":\"BOOK\",\"rating\":4,\"comment\":\"Useful book\"}"
```

### 5.12 Get reviews by target
```bash
# USER reviews
USER1_ID=$(curl -s "http://localhost:8080/api/exchanges/incoming" -H "Authorization: Bearer $TOKEN1" | jq -r '.data[0].ownerId')
curl -s "http://localhost:8080/api/reviews?targetType=USER&targetId=$USER1_ID"

# BOOK reviews
curl -s "http://localhost:8080/api/reviews?targetType=BOOK&targetId=$BOOK_ID"
```



