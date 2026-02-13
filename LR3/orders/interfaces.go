package main

// =========================================================
// Файл: interfaces.go
// Описание: Интерфейсы системы (контракты)
// =========================================================

// OrderValidator - контракт для валидации заказов
// ИСПРАВЛЕНО (SRP): валидация выделена в отдельный компонент
type OrderValidator interface {
	Validate(order Order) error
}

// PriceCalculator - контракт для расчёта стоимости заказа
// ИСПРАВЛЕНО (SRP): расчёты выделены в отдельный компонент
type PriceCalculator interface {
	Calculate(order Order) (float64, error)
}

// OrderRepository - контракт для работы с хранилищем заказов
// ИСПРАВЛЕНО (DIP): зависимость от абстракции, а не от конкретной БД
type OrderRepository interface {
	Save(order Order, total float64) error
	Exists(orderID string) bool
}

// Notifier - контракт для отправки уведомлений через любой канал
// ДОБАВЛЕНО: интерфейс для поддержки множественных каналов (Email, Telegram, Log)
type Notifier interface {
	Notify(recipient string, subject string, message string) error
}

// NotificationService определяет контракт для комплексной отправки уведомлений
// ДОБАВЛЕНО: сервис для отправки во все каналы одновременно
type NotificationService interface {
	NotifyAll(summary OrderSummary, order Order) error
}

// WorkerOrderProcessor - контракт для работников, обрабатывающих заказы
// ИСПРАВЛЕНО (ISP): разделена на 4 отдельных интерфейса вместо одного большого
type WorkerOrderProcessor interface {
	ProcessOrder()
}

// MeetingAtten - контракт для участников собраний
// ИСПРАВЛЕНО (ISP): отдельный интерфейс для человеческих действий
type MeetingAttendee interface {
	AttendMeeting()
}

// Restable - контракт для отдыха сотрудников
// ИСПРАВЛЕНО (ISP): отдельный интерфейс для отдыха
type Restable interface {
	GetRest()
}

// TimeWaster - контракт для проверки прогулов
// ИСПРАВЛЕНО (ISP): отдельный интерфейс для времяпрепровождения
type TimeWaster interface {
	SwingingTheLead()
}
