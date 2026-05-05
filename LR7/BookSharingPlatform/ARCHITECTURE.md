# Clean Architecture в `com.example.bookexchange`

## Правило зависимостей

В проекте реализован вариант Clean Architecture с портами/адаптерами:

`api -> controllers -> controllers.port <- clients`,  
а `models` и `shared` используются как внутренние контракты и общие компоненты.

Внутренний слой (`controllers`, `models`) не зависит от HTTP и JPA-деталей.

## Слои и пакеты

### 1) `api` (delivery, REST)

- Пакет: `com.example.bookexchange.api`
- Ответственность:
  - `@RestController` маршруты
  - валидация web DTO
  - преобразование request/response
  - возврат `ApiResponse<T>`
- Классы:
  - `AuthApiController`
  - `BookApiController`
  - `InventoryApiController`
  - `ExchangeApiController`
  - `ReviewApiController`
  - `GlobalExceptionHandler`

### 2) `controllers` (use-cases, бизнес-логика)

- Пакет: `com.example.bookexchange.controllers`
- Ответственность:
  - сценарии предметной области (регистрация, каталог, инвентарь, обмен, рейтинг)
  - применение бизнес-правил
- Классы:
  - `AuthUseCase`
  - `BookUseCase`
  - `InventoryUseCase`
  - `ExchangeUseCase`
  - `ReviewUseCase`

### 3) `controllers.port` (абстракции портов)

- Пакет: `com.example.bookexchange.controllers.port`
- Интерфейсы:
  - `UserGateway`, `BookGateway`, `InventoryGateway`, `ExchangeGateway`, `ReviewGateway`
- Use-case слой зависит только от этих интерфейсов (DIP).

### 4) `clients` (инфраструктурные адаптеры)

- Пакет: `com.example.bookexchange.clients`
- Содержимое:
  - `entity/*Entity` — JPA сущности
  - `spring/*JpaRepository` — Spring Data репозитории
  - `adapter/*GatewayAdapter` — реализации портов (маппинг domain <-> persistence)

### 5) `models` (domain)

- Пакет: `com.example.bookexchange.models`
- Чистые доменные модели:
  - `User`, `Book`, `InventoryItem`, `ExchangeRequest`, `Review`
  - `InventoryStatus`, `ExchangeStatus`
  - `AuthToken`
- Здесь нет JPA/Spring аннотаций.

### 6) `shared` (cross-cutting)

- Пакет: `com.example.bookexchange.shared`
- Содержимое:
  - `security`: `JwtUtil`, `JwtProperties`, `JwtAuthenticationFilter`
  - `config`: `SecurityConfig`, `OpenApiConfig`
  - `exception`: `BusinessException`, `ConflictException`, `NotFoundException`, `UnauthorizedException`
  - `api`: `ApiResponse<T>`

## Поток запроса

1. HTTP-запрос приходит в `api`.
2. Контроллер валидирует вход и вызывает соответствующий use-case из `controllers`.
3. Use-case обращается к интерфейсам портов (`controllers.port`).
4. Реализация портов в `clients.adapter` работает с JPA через `clients.spring`.
5. Результат возвращается в API DTO, ошибки маппятся `GlobalExceptionHandler`.

## Примеры ключевых бизнес-правил

- Нельзя запросить обмен, если инвентарь не в статусе `AVAILABLE`.
- Нельзя запросить собственную книгу.
- Отзыв можно оставить только после `TRANSFERRED` или `COMPLETED`.
- Рейтинг пользователя пересчитывается по отзывам.
- Рейтинг книги также пересчитывается по отзывам типа `BOOK`.
- История перемещений фиксируется через `BookMovement` и доступна через use-case/API.

## Применённые паттерны

- Clean Architecture
- Ports & Adapters (Hexagonal style)
- Repository (Spring Data)
- Dependency Inversion Principle (через `controllers.port`)
- DTO + Mapper на границах API/Infrastructure
- Centralized Exception Handling
