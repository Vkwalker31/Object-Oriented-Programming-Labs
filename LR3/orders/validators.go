package main

import "errors"

// =========================================================
// Файл: models.go
// Описание: Валидация входных данных заказов перед обработкой.
// =========================================================

// BasicOrderValidator - реализация валидации заказов
// ИСПРАВЛЕНО (SRP): выделена в отдельный класс, отвечает только за валидацию
type BasicOrderValidator struct{}

// NewBasicOrderValidator - создание нового экземпляра валидатора
func NewBasicOrderValidator() *BasicOrderValidator {
	return &BasicOrderValidator{}
}

// Validate - проверка корректности заказа
// ИСПРАВЛЕНО: заменена встроенная в Process() валидация
func (v *BasicOrderValidator) Validate(order Order) error {
	// Заказ должен содержать хотя бы один товар
	if len(order.Items) == 0 {
		return errors.New("order must have at least one item")
	}

	// Город доставки обязателен
	if order.Destination.City == "" {
		return errors.New("destination city is required")
	}

	// Email клиента обязателен для уведомлений
	if order.ClientEmail == "" {
		return errors.New("client email is required")
	}

	// Бюджетные заказы ограничены 3 товарами
	if order.Type == "Budget" && len(order.Items) > 3 {
		return errors.New("budget orders cannot have more than 3 items")
	}

	// Международные заказы не могут быть в "Никуда"
	if order.Type == "International" && order.Destination.City == "Nowhere" {
		return errors.New("cannot ship to Nowhere")
	}

	return nil
}
