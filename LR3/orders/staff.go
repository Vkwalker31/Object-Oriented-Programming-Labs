package main

import "fmt"

// =========================================================
// Файл: staff.go
// Описание: Система управления персоналом склада.
// =========================================================

// HumanManager - менеджер в организации
// ИСПРАВЛЕНО (ISP): реализует все необходимые интерфейсы для человека
type HumanManager struct{}

// ProcessOrder - обработка заказы (как работник)
func (h HumanManager) ProcessOrder() {
	fmt.Println("Manager is processing logic...")
}

// AttendMeeting участвует в собраниях (как человек)
func (h HumanManager) AttendMeeting() {
	fmt.Println("Manager is sleeping at the meeting...")
}

// GetRest - отдых (как все работники)
func (h HumanManager) GetRest() {
	fmt.Println("Manager is taking a break...")
}

// SwingingTheLead - бездельничает на работе (как человек)
func (h HumanManager) SwingingTheLead() {
	fmt.Println("Manager is watching reels...")
}

// RobotPacker - робот для упаковки заказов
// ИСПРАВЛЕНО (ISP): реализует только нужные интерфейсы (не AttendMeeting, не SwingingTheLead)
type RobotPacker struct {
	Model string
}

// ProcessOrder обработка заказов (как работник)
func (r RobotPacker) ProcessOrder() {
	fmt.Println("🤖 Robot " + r.Model + " is packing boxes...")
}

// GetRest - обслуживание (как все работники)
func (r RobotPacker) GetRest() {
	fmt.Println("🔧 Robot " + r.Model + " is in maintenance...")
}

// ManageWarehouse - управление складом, указывая работникам обрабатывать заказы
func ManageWarehouse(processors []WorkerOrderProcessor) {
	fmt.Println("\n--- Warehouse Shift: Order Processing ---")
	for _, processor := range processors {
		processor.ProcessOrder()
	}
}

// HoldMeeting - собрание с участниками
// ИСПРАВЛЕНО (ISP): только люди могут ходить на собрания
func HoldMeeting(attendees []MeetingAttendee) {
	fmt.Println("\n--- Company Meeting ---")
	for _, attendee := range attendees {
		attendee.AttendMeeting()
	}
}

// BreakTime - перерыв для всех работников
func BreakTime(workers []Restable) {
	fmt.Println("\n--- Break Time ---")
	for _, worker := range workers {
		worker.GetRest()
	}
}

// CheckSlackers - проверка, не бездельничают ли сотрудники
// ИСПРАВЛЕНО (ISP): только люди могут бездельничать
func CheckSlackers(suspects []TimeWaster) {
	fmt.Println("\n--- Security Check ---")
	for _, suspect := range suspects {
		suspect.SwingingTheLead()
	}
}
