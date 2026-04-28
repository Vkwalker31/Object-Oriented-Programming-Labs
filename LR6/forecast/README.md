# Weather Forecast API (LR6)

Сервис погоды с Dependency Injection и модульными тестами.

## Что реализовано
- Выбор провайдера: `openweather` или `google` через query-параметр `provider`.
- Текущая погода по координатам (`lat`, `lon`) или по городу (`city`).
- Прогноз погоды для локации.
- Пакетное получение текущей температуры для нескольких локаций.
- Поддержка городов: `Minsk`, `London`, `Tokyo`, `Shanghai`, `Warsaw`.
- Swagger для всех роутов.

## Запуск
```bash
go run main.go
```

Swagger UI: [http://localhost:8080/swagger/index.html](http://localhost:8080/swagger/index.html)

## Тесты
```bash
go test ./...
go test ./... -cover
```

## Переменные окружения
```env
OPENWEATHER_API_KEY=your_openweather_api_key
OPENWEATHER_BASE_URL=https://api.openweathermap.org/data/2.5/weather
GOOGLE_WEATHER_API_KEY=your_google_api_key
# alias also supported:
# GOOGLE_API_KEY=your_google_api_key
GOOGLE_WEATHER_BASE_URL=https://weather.googleapis.com/v1
```

Важно: не коммитьте реальные API-ключи в Git. Храните их только в локальном `.env`.

## Роуты
- `GET /api/v1/weather` — текущая погода.  
  Параметры: `lat`+`lon` **или** `city`, опционально `provider`.
- `GET /api/v1/forecast` — прогноз погоды.  
  Параметры: `lat`+`lon` **или** `city`, опционально `provider`.
- `POST /api/v1/weather/batch` — текущая погода для нескольких локаций.  
  Body: массив элементов вида `{"city":"Minsk"}` или `{"lat":"53.9","lon":"27.56"}`, опционально `provider`.
