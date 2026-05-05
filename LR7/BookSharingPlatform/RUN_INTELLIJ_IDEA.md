# Запуск проекта в IntelliJ IDEA

## 1. Открытие проекта

1. Запустите **IntelliJ IDEA**.
2. **File → Open** (или **Open** на стартовом экране).
3. Укажите папку проекта: `BookSharingPlatform` (та, где лежит `pom.xml`).
4. Выберите **Open as Project**.
5. Если IDEA предложит загрузить Maven-зависимости — нажмите **Load Maven Project** / **Import Maven Project** и дождитесь окончания индексации.

## 2. Настройка JDK

1. **File → Project Structure** (или `Ctrl+Alt+Shift+S`).
2. В разделе **Project** выберите **Project SDK**: **Java 17** (или 21).  
   Если JDK нет в списке — **Add SDK → Download JDK** → выберите версию 17 или 21 (например, Eclipse Temurin).
3. **Project language level** — не ниже **17**.
4. Нажмите **Apply** и **OK**.

Проверка: в правом нижнем углу IDEA должен отображаться выбранный JDK (например, "17").

## 3. Запуск тестов

1. В панели **Project** откройте папку `src/test/java/com/example/bookexchange/`.
2. Запуск **всех тестов**:
   - Правый клик по пакету **bookexchange** (или по папке `test`) → **Run 'All Tests'**.
3. Запуск **одного тестового класса**:
   - Правый клик по классу (например, `AuthUseCaseTest`) → **Run 'AuthUseCaseTest'**.
4. Запуск **одного теста**:
   - Откройте класс, слева от имени метода нажмите зелёную стрелку → **Run**.

Тесты используют профиль `test` и in-memory H2, PostgreSQL для этого не нужен. Все тесты должны завершиться успешно (зелёная галочка).

## 4. Запуск приложения (с базой PostgreSQL)

### Подготовка БД

1. Установите и запустите **PostgreSQL 15+**.
2. Создайте базу и при необходимости пользователя, например в **pgAdmin** или **psql**:

```sql
CREATE DATABASE booksharing;
-- По умолчанию в application.yml: пользователь postgres, пароль postgres
```

Если у вас другие логин/пароль — измените в `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/booksharing
    username: ваш_пользователь
    password: ваш_пароль
```

### Запуск через IntelliJ

1. Откройте класс `BookExchangeApplication` (`src/main/java/com/example/bookexchange/BookExchangeApplication.java`).
2. Справа от объявления класса или от метода `main` нажмите зелёную стрелку → **Run 'BookExchangeApplication'**.

Либо через Maven (если в IDEA настроен Maven):

1. Откройте панель **Maven** (справа или **View → Tool Windows → Maven**).
2. Разверните **book-sharing-platform → Lifecycle**.
3. Дважды щёлкните **spring-boot:run**.

В консоли должно появиться сообщение вида: `Started BookExchangeApplication in ... seconds`.

### Проверка работы

- Базовый URL: **http://localhost:8080/api**
- Swagger UI: **http://localhost:8080/api/swagger-ui.html**  
  Откройте в браузере — там перечислены все эндпоинты, можно вызывать их из интерфейса.
- Регистрация: `POST http://localhost:8080/api/auth/register` с телом, например:
  ```json
  { "username": "testuser", "email": "user@test.com", "password": "password123" }
  ```
- После логина используйте выданный JWT в заголовке: `Authorization: Bearer <токен>`.

## 5. (Опционально) Секрет JWT

По умолчанию в `application.yml` задан тестовый ключ. Для более безопасного запуска:

1. **Run → Edit Configurations**.
2. Выберите конфигурацию **BookExchangeApplication**.
3. В поле **Environment variables** добавьте, например:  
   `JWT_SECRET=ваш-секрет-не-короче-32-символов`.
4. Сохраните и запустите приложение.

## 6. Частые проблемы

| Проблема | Решение |
|----------|--------|
| Тесты падают с ошибкой БД | Убедитесь, что активен профиль **test** (в тестах используется H2). В **Run Configuration** для тестов в поле **Active profiles** должно быть `test` (часто подставляется автоматически из `@ActiveProfiles("test")`). |
| Приложение не стартует: "Connection refused" к БД | Запустите PostgreSQL, проверьте хост/порт (5432), имя БД, логин и пароль в `application.yml`. |
| Maven-зависимости не подтягиваются | Правый клик по `pom.xml` → **Maven → Reload project**. Проверьте доступ в интернет. |
| Ошибки компиляции в мапперах (MapStruct) | Выполните **Build → Rebuild Project**. Убедитесь, что в **Project Structure → Modules** для модуля включена аннотация от MapStruct (обычно подхватывается из Maven). |

После выполнения шагов 1–2 и 3 тесты должны проходить; после шага 4 приложение должно запускаться и открываться Swagger по указанному адресу.
