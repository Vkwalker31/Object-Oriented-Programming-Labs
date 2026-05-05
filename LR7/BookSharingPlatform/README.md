# Book Exchange Platform (Clean Architecture)

Backend для платформы обмена книгами на Java 17 + Spring Boot 3.x.  
Основной код новой версии находится в пакете `com.example.bookexchange`.

## Что реализовано

- Регистрация и логин пользователя (JWT).
- Каталог книг: создание и поиск по `title/author/isbn`.
- Личный инвентарь: добавление книги в фонд, изменение статуса.
- Обмены: запрос книги, просмотр входящих/исходящих, смена статуса.
- Отзывы и рейтинг пользователя после успешного обмена.

## Стек

- Java 17
- Spring Boot 3.2 (`web`, `data-jpa`, `security`, `validation`)
- PostgreSQL (runtime), H2 (runtime для локальной/быстрой разработки)
- SpringDoc OpenAPI (Swagger UI)
- JWT (`jjwt`)
- Lombok
- JUnit 5 + Mockito
- Maven

## Структура (Clean Architecture)

- `api` — REST контроллеры, web DTO, HTTP-ответы
- `controllers` — use-cases (бизнес-логика)
- `controllers.port` — абстракции портов (gateway interfaces)
- `clients` — JPA entities, Spring Data репозитории, adapter-реализации портов
- `models` — чистые доменные модели (без JPA/Spring аннотаций)
- `shared` — JWT, exceptions, общие response-объекты, конфиги

Правило зависимостей соблюдается: `api -> controllers -> controllers.port <- clients`, а `models/shared` используются как общие внутренние контракты.

## Запуск в IntelliJ IDEA

1. **File -> Open** и выберите папку `BookSharingPlatform` (где `pom.xml`).
2. Подтвердите import Maven-проекта.
3. Установите Project SDK = **Java 17**.
4. Запустите класс `com.example.bookexchange.BookExchangeApplication`.

## Запуск через Maven

1. Поднимите PostgreSQL и создайте БД `booksharing`.
2. При необходимости задайте `JWT_SECRET` (не короче 32 символов):
   - PowerShell:  
     `setx JWT_SECRET "your-256-bit-secret-key-for-signing-must-be-at-least-32-chars"`
3. Запустите:

```bash
mvn clean test
mvn spring-boot:run
```

## URL приложения

- Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

## Краткое API

- `POST /auth/register`
- `POST /auth/login`
- `GET /books`
- `GET /books/{bookId}`
- `POST /books`
- `POST /inventory`
- `GET /inventory/me`
- `PATCH /inventory/{inventoryId}/status?status=AVAILABLE|BUSY`
- `POST /exchanges`
- `PATCH /exchanges/{exchangeId}/status`
- `GET /exchanges/incoming`
- `GET /exchanges/outgoing`
- `GET /exchanges/history`
- `POST /reviews`
- `GET /reviews?targetType=USER|BOOK&targetId={id}`

Для защищенных endpoint используйте header:  
`Authorization: Bearer <jwt-token>`

## Quick Start: проверка API за 5 минут

Пример через Swagger:

1. Откройте `http://localhost:8080/api/swagger-ui.html`.
2. Выполните `POST /auth/register` для пользователя #1.
3. Выполните `POST /auth/register` для пользователя #2.
4. Выполните `POST /auth/login` для пользователя #1, скопируйте JWT.
5. Нажмите **Authorize** и вставьте: `Bearer <token_user1>`.
6. Выполните `POST /books` (создайте книгу), затем `POST /inventory` (добавьте в фонд).
7. Разлогиньтесь, логин пользователя #2 -> новый JWT -> Authorize.
8. Выполните `POST /exchanges` с `inventoryId` из шага 6.
9. Под пользователем #1 откройте `GET /exchanges/incoming` и `PATCH /exchanges/{id}/status`:
   - `APPROVED`, затем `TRANSFERRED`, затем `COMPLETED`.
10. Под пользователем #2 выполните `POST /reviews` на этот `exchangeId`.

Ожидаемый результат:
- обмен создается только для инвентаря со статусом `AVAILABLE`;
- после создания запроса инвентарь у владельца становится `BUSY`;
- после `COMPLETED` инвентарь возвращается в `AVAILABLE`;
- отзыв доступен только после успешного обмена (`TRANSFERRED`/`COMPLETED`);
- поддержаны отзывы и для пользователей (`USER`), и для книг (`BOOK`).
