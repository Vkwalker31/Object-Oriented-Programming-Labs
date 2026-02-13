package main

import (
	"fmt"
	"sync"
)

// CachedOrderRepository - оборачивание любого OrderRepository и добавление кэширования
// ДОБАВЛЕНО: Decorator паттерн для прозрачного кэширования заказов
// ФУНКЦИОНАЛ: предотвращает повторные записи одного и того же заказа в БД
type CachedOrderRepository struct {
	backend OrderRepository
	cache   map[string]bool
	mu      sync.RWMutex
}

// NewCachedOrderRepository создание кэшированного репозитория
func NewCachedOrderRepository(backend OrderRepository) *CachedOrderRepository {
	return &CachedOrderRepository{
		backend: backend,
		cache:   make(map[string]bool),
	}
}

// Save - сохранение заказа, но только если его ещё нет в кэше
// ДОБАВЛЕНО: первый вызов пишет в БД, второй пропускает (использует кэш)
func (r *CachedOrderRepository) Save(order Order, total float64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if r.cache[order.ID] {
		fmt.Printf("⚠️  Order %s already in cache. Skipping DB save.\n", order.ID)
		return nil
	}

	if err := r.backend.Save(order, total); err != nil {
		return err
	}

	r.cache[order.ID] = true
	return nil
}

// Exists - проверка, находится ли заказ в кэше
func (r *CachedOrderRepository) Exists(orderID string) bool {
	r.mu.RLock()
	defer r.mu.RUnlock()

	return r.cache[orderID]
}
