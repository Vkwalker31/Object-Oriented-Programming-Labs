package main

// =========================================================
// Файл: models.go
// Описание: Модели данных системы.
// =========================================================

// Item - товар в заказе
type Item struct {
	ID    string
	Name  string
	Price float64
}

// Address - адрес доставки
type Address struct {
	City   string
	Street string
	Zip    string
}

// DiscountCard - дисконтная карта клиента
// ДОБАВЛЕНО: поддержка трёх типов карт для скидок
type DiscountCard struct {
	Type string // Тип карты: "Gold" (15%), "Silver" (10%), "Newbie" (0%)
}

// Order - заказ
type Order struct {
	ID           string
	Items        []Item
	Type         string // "Standard", "Premium", "Budget", "International"
	ClientEmail  string
	Destination  Address
	DiscountCard *DiscountCard // ДОБАВЛЕНО: опциональная дисконтная карта (nil если нет)
}

// OrderSummary содержит результаты обработки заказа
// ДОБАВЛЕНО: структура для возврата результатов из процессора
type OrderSummary struct {
	OrderID string  // ID обработанного заказа
	Total   float64 // Итоговая стоимость после всех скидок и налогов
}
