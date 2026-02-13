package main

import (
	"errors"
	"fmt"
)

// =========================================================
// Файл: calcurators.go
// Описание: Основная бизнес-логика расчёта стоимости заказов.
// =========================================================

// OrderTypeStrategy - интерфейс для стратегий расчёта по типам заказов
// ДОБАВЛЕНО: паттерн Strategy для избежания switch-case (Open/Closed Principle)
type OrderTypeStrategy interface {
	ApplyModifiers(subtotal float64) float64
}

// StandardStrategy - расчёт для стандартных заказов
// Стандартный заказ: +20% (налог)
type StandardStrategy struct{}

func (s *StandardStrategy) ApplyModifiers(subtotal float64) float64 {
	// subtotal * 1.2 = добавить 20% налога
	return subtotal * 1.2
}

// PremiumStrategy - расчёт для премиум заказов
// Премиум заказ: -10% скидка + 20% налог = финальная цена
type PremiumStrategy struct{}

func (s *PremiumStrategy) ApplyModifiers(subtotal float64) float64 {
	// Сначала скидка 10%, потом налог 20%
	return (subtotal * 0.9) * 1.2
}

// BudgetStrategy - расчёт для бюджетных заказов
// Бюджетный заказ: без налогов (0%)
type BudgetStrategy struct{}

func (s *BudgetStrategy) ApplyModifiers(subtotal float64) float64 {
	// Без налогов - вернуть как есть
	return subtotal
}

// InternationalStrategy - расчёт для международных заказов
// Международный заказ: +50% (доставка и таможня)
type InternationalStrategy struct{}

func (s *InternationalStrategy) ApplyModifiers(subtotal float64) float64 {
	// subtotal * 1.5 = добавить 50% за международную доставку
	return subtotal * 1.5
}

// DiscountStrategy - интерфейс для стратегий дисконтных карт
// ДОБАВЛЕНО: поддержка дисконтных карт (Gold, Silver, Newbie)
type DiscountStrategy interface {
	ApplyDiscount(subtotal float64) float64
}

// GoldCardStrategy - скидка для Gold карты (15%)
type GoldCardStrategy struct{}

func (g *GoldCardStrategy) ApplyDiscount(subtotal float64) float64 {
	// subtotal * 0.85 = скидка 15%
	return subtotal * 0.85
}

// SilverCardStrategy - скидка для Silver карты (10%)
type SilverCardStrategy struct{}

func (s *SilverCardStrategy) ApplyDiscount(subtotal float64) float64 {
	// subtotal * 0.90 = скидка 10%
	return subtotal * 0.90
}

// NewbieCardStrategy - скидка для Newbie карты (0%)
type NewbieCardStrategy struct{}

func (n *NewbieCardStrategy) ApplyDiscount(subtotal float64) float64 {
	// Без скидки - вернуть как есть
	return subtotal
}

// NoCardStrategy - "скидка" при отсутствии карты
type NoCardStrategy struct{}

func (n *NoCardStrategy) ApplyDiscount(subtotal float64) float64 {
	// Без карты - без скидок
	return subtotal
}

// SmartPriceCalculator - вычисление цены заказа с использованием стратегий
// ИСПРАВЛЕНО (OCP): используется Strategy паттерн вместо switch-case
// ДОБАВЛЕНО: поддержка дисконтных карт
type SmartPriceCalculator struct {
	orderStrategies    map[string]OrderTypeStrategy
	discountStrategies map[string]DiscountStrategy
}

// NewSmartPriceCalculator - калькулятор со встроенными стратегиями
func NewSmartPriceCalculator() *SmartPriceCalculator {
	return &SmartPriceCalculator{
		orderStrategies: map[string]OrderTypeStrategy{
			"Standard":      &StandardStrategy{},
			"Premium":       &PremiumStrategy{},
			"Budget":        &BudgetStrategy{},
			"International": &InternationalStrategy{},
		},
		discountStrategies: map[string]DiscountStrategy{
			"Gold":   &GoldCardStrategy{},
			"Silver": &SilverCardStrategy{},
			"Newbie": &NewbieCardStrategy{},
		},
	}
}

// Calculate - вычисление итоговой цены заказа
func (c *SmartPriceCalculator) Calculate(order Order) (float64, error) {
	var subtotal float64
	for _, item := range order.Items {
		subtotal += item.Price
	}

	if order.DiscountCard != nil {
		strategy, exists := c.discountStrategies[order.DiscountCard.Type]
		if !exists {
			// Неизвестный тип карты
			return 0, fmt.Errorf("unknown discount card type: %s", order.DiscountCard.Type)
		}

		subtotal = strategy.ApplyDiscount(subtotal)
		fmt.Printf("Applied %s card discount. New subtotal: %.2f\n", order.DiscountCard.Type, subtotal)
	}

	strategy, exists := c.orderStrategies[order.Type]
	if !exists {
		// Неизвестный тип заказа
		return 0, errors.New("unknown order type")
	}

	total := strategy.ApplyModifiers(subtotal)
	return total, nil
}

// RegisterOrderStrategy - новая стратегия расчёта для типа заказа
// ИСПРАВЛЕНО (OCP): позволяет добавлять новые типы заказов без изменения кода
func (c *SmartPriceCalculator) RegisterOrderStrategy(orderType string, strategy OrderTypeStrategy) {
	c.orderStrategies[orderType] = strategy
}

// RegisterDiscountStrategy - новая стратегия скидки для типа карты
// ДОБАВЛЕНО: позволяет добавлять новые типы дисконтных карт во время runtime
func (c *SmartPriceCalculator) RegisterDiscountStrategy(cardType string, strategy DiscountStrategy) {
	c.discountStrategies[cardType] = strategy
}
