package main

import (
	"fmt"
)

// =========================================================
// Файл: processor.go
// Описание: Оркестрация обработки заказов (контроллер).
// =========================================================

// ModernOrderProcessor - обработка заказов, используя Dependency Injection
// ИСПРАВЛЕНО (SRP): разделена ответственность между компонентами
// ИСПРАВЛЕНО (DIP): все зависимости внедряются, а не создаются в процессе
type ModernOrderProcessor struct {
	validator  OrderValidator
	calculator PriceCalculator
	repository OrderRepository
	notifier   NotificationService
}

// NewModernOrderProcessor - процессор с внедренными зависимостями
// ИСПРАВЛЕНО (DIP): Dependency Injection через конструктор
func NewModernOrderProcessor(
	validator OrderValidator,
	calculator PriceCalculator,
	repository OrderRepository,
	notifier NotificationService,
) *ModernOrderProcessor {
	return &ModernOrderProcessor{
		validator:  validator,
		calculator: calculator,
		repository: repository,
		notifier:   notifier,
	}
}

// Process - обработка заказа через все этапы
// ИСПРАВЛЕНО: теперь это только оркестрация, логика выделена в компоненты
func (p *ModernOrderProcessor) Process(order Order) error {
	fmt.Printf("\n--- Processing Order %s ---\n", order.ID)

	if err := p.validator.Validate(order); err != nil {
		return fmt.Errorf("validation error: %w", err)
	}

	total, err := p.calculator.Calculate(order)
	if err != nil {
		return fmt.Errorf("calculation error: %w", err)
	}
	fmt.Printf("Total calculated: $%.2f\n", total)

	if err := p.repository.Save(order, total); err != nil {
		return fmt.Errorf("repository error: %w", err)
	}

	summary := OrderSummary{
		OrderID: order.ID,
		Total:   total,
	}
	if err := p.notifier.NotifyAll(summary, order); err != nil {
		return fmt.Errorf("notification error: %w", err)
	}

	fmt.Printf("Order %s processed successfully!\n", order.ID)
	return nil
}
