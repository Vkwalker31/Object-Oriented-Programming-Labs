package main

import "errors"

// =========================================================
// Файл: validators.go
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
	if len(order.Items) == 0 {
		return errors.New("order must have at least one item")
	}
	if order.Destination.City == "" {
		return errors.New("destination city is required")
	}
	if order.ClientEmail == "" {
		return errors.New("client email is required")
	}
	if order.Type == "Budget" && len(order.Items) > 3 {
		return errors.New("budget orders cannot have more than 3 items")
	}
	if order.Type == "International" && order.Destination.City == "Nowhere" {
		return errors.New("cannot ship to Nowhere")
	}

	return nil
}
