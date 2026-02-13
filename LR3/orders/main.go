package main

import (
	"fmt"
	"log"
)

// =========================================================
// Файл: main.go
// Описание: Точка входа в приложение.
// =========================================================

func main() {
	fmt.Println("╔════════════════════════════════════════╗")
	fmt.Println("║   ORDERS SYSTEM v2.0 (SOLID Edition)   ║")
	fmt.Println("╚════════════════════════════════════════╝\n")

	// === SETUP: Dependency Injection ===
	validator := NewBasicOrderValidator()
	calculator := NewSmartPriceCalculator()
	database := NewMySQLDatabase()
	repository := NewCachedOrderRepository(database)

	emailNotifier := NewEmailNotifier("smtp.google.com")
	telegramNotifier := NewTelegramNotifier("bot123456:ABC-DEF")
	fileLogger := NewFileLogger("events.log")
	notificationService := NewCompositeNotificationService(emailNotifier, telegramNotifier, fileLogger)

	processor := NewModernOrderProcessor(validator, calculator, repository, notificationService)

	// === TEST: Обработка 3 заказов ===
	orders := []Order{
		{
			ID:           "ORD-001",
			Type:         "Premium",
			Items:        []Item{{ID: "1", Name: "Laptop", Price: 1500}},
			ClientEmail:  "john@example.com",
			Destination:  Address{City: "New York", Street: "5th Ave", Zip: "10001"},
			DiscountCard: &DiscountCard{Type: "Gold"},
		},
		{
			ID:           "ORD-002",
			Type:         "International",
			Items:        []Item{{ID: "2", Name: "Phone", Price: 800}},
			ClientEmail:  "jane@example.com",
			Destination:  Address{City: "London", Street: "Baker St", Zip: "NW1"},
			DiscountCard: &DiscountCard{Type: "Silver"},
		},
		{
			ID:           "ORD-003",
			Type:         "Budget",
			Items:        []Item{{ID: "3", Name: "Mouse", Price: 50}},
			ClientEmail:  "bob@example.com",
			Destination:  Address{City: "Paris", Street: "Rue de Rivoli", Zip: "75001"},
			DiscountCard: nil,
		},
	}

	for _, order := range orders {
		if err := processor.Process(order); err != nil {
			log.Printf("❌ Error: %v\n", err)
		}
	}

	human := HumanManager{}
	robot := RobotPacker{Model: "R2D2"}

	ManageWarehouse([]WorkerOrderProcessor{human, robot})
	HoldMeeting([]MeetingAttendee{human})
	BreakTime([]Restable{human, robot})
	CheckSlackers([]TimeWaster{human})
}
