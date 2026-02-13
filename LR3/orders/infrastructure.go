package main

import (
	"fmt"
	"os"
	"time"
)

// RandomSQLDatabase - хранилище заказов в текстовом файле
// ИСПРАВЛЕНО (DIP): теперь это просто реализация OrderRepository интерфейса
type RandomSQLDatabase struct {
	ConnectionString string
}

// NewMySQLDatabase - создание нового подключения к БД
func NewMySQLDatabase() *RandomSQLDatabase {
	return &RandomSQLDatabase{
		ConnectionString: "random://root:password@localhost:228/shop",
	}
}

// Save - сохранение заказа в текстовый файл (имитация БД)
func (db *RandomSQLDatabase) Save(order Order, total float64) error {
	fmt.Println("🔌 Connecting to RandomSQL...")
	time.Sleep(500 * time.Millisecond)

	file, err := os.OpenFile("orders_db.txt", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		return err
	}
	defer file.Close()

	record := fmt.Sprintf("[%s] ID: %s | Type: %s | Total: %.2f\n",
		time.Now().Format(time.RFC3339), order.ID, order.Type, total)

	if _, err := file.WriteString(record); err != nil {
		return err
	}

	fmt.Println("Order saved to database.")
	return nil
}

// Exists - проверка существование заказа в БД (заглушка)
func (db *RandomSQLDatabase) Exists(orderID string) bool {
	return false
}

// EmailNotifier отправка уведомления по email
// ДОБАВЛЕНО: реализация Notifier интерфейса для Email канала
type EmailNotifier struct {
	Server string
}

// NewEmailNotifier - уведомитель для отправки писем
func NewEmailNotifier(server string) *EmailNotifier {
	return &EmailNotifier{Server: server}
}

// Notify - отправка email уведомления
func (e *EmailNotifier) Notify(recipient string, subject string, message string) error {
	fmt.Printf("[EMAIL] To: %s | Subject: %s\n", recipient, subject)
	fmt.Printf("   Message: %s\n\n", message)
	return nil
}

// TelegramNotifier - отправка уведомлений в Telegram
// ДОБАВЛЕНО: новый канал уведомлений - Telegram для менеджеров
type TelegramNotifier struct {
	BotToken string
}

// NewTelegramNotifier - уведомитель для Telegram
func NewTelegramNotifier(botToken string) *TelegramNotifier {
	return &TelegramNotifier{BotToken: botToken}
}

// Notify - отправка сообщений в Telegram
func (t *TelegramNotifier) Notify(recipient string, subject string, message string) error {
	fmt.Printf("[TELEGRAM] To: %s\n", recipient)
	fmt.Printf("   [%s] %s\n\n", subject, message)
	return nil
}

// FileLogger - логи события в текстовый файл
// ДОБАВЛЕНО: система логирования для аудита и отладки
type FileLogger struct {
	// FilePath: путь к файлу логов
	FilePath string
}

// NewFileLogger - создание логгера, пишущего в файл
func NewFileLogger(filePath string) *FileLogger {
	return &FileLogger{FilePath: filePath}
}

// Notify - запись событий в файл логов
func (f *FileLogger) Notify(recipient string, subject string, message string) error {
	file, err := os.OpenFile(f.FilePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		return err
	}
	defer file.Close()

	logEntry := fmt.Sprintf("[%s] %s - %s\n",
		time.Now().Format(time.RFC3339), subject, message)

	if _, err := file.WriteString(logEntry); err != nil {
		return err
	}

	fmt.Printf("[LOG] Event logged to %s\n\n", f.FilePath)
	return nil
}

// CompositeNotificationService - отправка уведомлений через все канали одновременно
// ДОБАВЛЕНО: сервис для оркестрации нескольких Notifier-ов
type CompositeNotificationService struct {
	notifiers []Notifier
}

// NewCompositeNotificationService - создание сервиса уведомлений
func NewCompositeNotificationService(notifiers ...Notifier) *CompositeNotificationService {
	return &CompositeNotificationService{notifiers: notifiers}
}

// NotifyAll - отправка всех уведомлений заинтересованным сторонам
// ДОБАВЛЕНО: отправляет различные сообщения через разные каналы:
// 1. Клиенту на Email - подтверждение заказа
// 2. Менеджеру в Telegram - оповещение о новом заказе
// 3. В логи - запись события для аудита
func (c *CompositeNotificationService) NotifyAll(summary OrderSummary, order Order) error {

	clientMessage := fmt.Sprintf("Your order %s is confirmed! Total: $%.2f",
		summary.OrderID, summary.Total)

	managerMessage := fmt.Sprintf("New order: %s | Total: $%.2f | Client: %s",
		summary.OrderID, summary.Total, order.ClientEmail)

	logMessage := fmt.Sprintf("Order %s processed successfully. Total: $%.2f",
		summary.OrderID, summary.Total)

	messages := []struct {
		recipient string
		subject   string
		message   string
	}{
		{order.ClientEmail, "Order Confirmation", clientMessage},
		{"@warehouse_manager", "New Order Alert", managerMessage},
		{"system", "ORDER_PROCESSED", logMessage},
	}

	for i, notifier := range c.notifiers {
		if i < len(messages) {
			if err := notifier.Notify(messages[i].recipient, messages[i].subject, messages[i].message); err != nil {
				fmt.Printf("Failed to notify: %v\n", err)
			}
		}
	}

	return nil
}
