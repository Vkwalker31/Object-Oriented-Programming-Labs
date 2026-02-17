package controllers

import (
	"errors"
	"testing"

	"clients"
	_ "models/weather"

	"github.com/shopspring/decimal"
)

type mockWeatherClient struct {
	temp     decimal.Decimal
	err      error
	forecast []clients.ForecastItem
}

func (m *mockWeatherClient) LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (decimal.Decimal, error) {
	return m.temp, m.err
}

func (m *mockWeatherClient) LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]clients.ForecastItem, error) {
	if m.err != nil {
		return nil, m.err
	}
	return m.forecast, nil
}

var _ clients.WeatherDataClient = (*mockWeatherClient)(nil)

func TestCurrentWeatherController_GetCurrentWeather_Success(t *testing.T) {
	expectedTemp := decimal.NewFromFloat(22.5)
	mock := &mockWeatherClient{
		temp: expectedTemp,
		err:  nil,
	}
	ctrl := NewCurrentWeatherController(mock)

	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	result, err := ctrl.GetCurrentWeather(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if !result.Temperature.Equal(expectedTemp) {
		t.Errorf("temperature = %v, want %v", result.Temperature, expectedTemp)
	}
}

func TestCurrentWeatherController_GetCurrentWeather_ClientError(t *testing.T) {
	clientErr := errors.New("provider unavailable")
	mock := &mockWeatherClient{
		temp: decimal.Zero,
		err:  clientErr,
	}
	ctrl := NewCurrentWeatherController(mock)

	lat := decimal.NewFromFloat(0)
	lon := decimal.NewFromFloat(0)

	result, err := ctrl.GetCurrentWeather(lat, lon)
	if err != clientErr {
		t.Fatalf("err = %v, want %v", err, clientErr)
	}
	if !result.Temperature.IsZero() {
		t.Error("expected zero temperature on error")
	}
}

func TestCurrentWeatherController_GetForecast_Success(t *testing.T) {
	mock := &mockWeatherClient{
		forecast: []clients.ForecastItem{
			{Date: "2025-02-17", Temperature: decimal.NewFromFloat(10.0)},
			{Date: "2025-02-18", Temperature: decimal.NewFromFloat(12.0)},
		},
		err: nil,
	}
	ctrl := NewCurrentWeatherController(mock)
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	result, err := ctrl.GetForecast(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if len(result.Entries) != 2 {
		t.Fatalf("len(entries) = %d, want 2", len(result.Entries))
	}
	if result.Entries[0].Date != "2025-02-17" || !result.Entries[0].Temperature.Equal(decimal.NewFromFloat(10.0)) {
		t.Errorf("first entry = %+v", result.Entries[0])
	}
	if result.Entries[1].Date != "2025-02-18" || !result.Entries[1].Temperature.Equal(decimal.NewFromFloat(12.0)) {
		t.Errorf("second entry = %+v", result.Entries[1])
	}
}

func TestCurrentWeatherController_GetForecast_ClientError(t *testing.T) {
	clientErr := errors.New("forecast service unavailable")
	mock := &mockWeatherClient{
		forecast: nil,
		err:      clientErr,
	}
	ctrl := NewCurrentWeatherController(mock)
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	result, err := ctrl.GetForecast(lat, lon)
	if err != clientErr {
		t.Fatalf("err = %v, want %v", err, clientErr)
	}
	if len(result.Entries) != 0 {
		t.Error("expected empty forecast on error")
	}
}

func TestCurrentWeatherController_GetForecast_Empty(t *testing.T) {
	mock := &mockWeatherClient{
		forecast: []clients.ForecastItem{},
		err:      nil,
	}
	ctrl := NewCurrentWeatherController(mock)
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	result, err := ctrl.GetForecast(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if len(result.Entries) != 0 {
		t.Error("expected empty forecast")
	}
}

func TestCurrentWeatherController_GetCurrentWeather_VariousTemperatures(t *testing.T) {
	tests := []struct {
		name        string
		temperature float64
	}{
		{"Zero", 0.0},
		{"Positive", 25.5},
		{"Negative", -15.3},
		{"Small positive", 0.1},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			expectedTemp := decimal.NewFromFloat(tt.temperature)
			mock := &mockWeatherClient{temp: expectedTemp}
			ctrl := NewCurrentWeatherController(mock)

			result, err := ctrl.GetCurrentWeather(
				decimal.NewFromFloat(53.9),
				decimal.NewFromFloat(27.5),
			)

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}
			if !result.Temperature.Equal(expectedTemp) {
				t.Errorf("temperature = %v, want %v", result.Temperature, expectedTemp)
			}
		})
	}
}

func TestCurrentWeatherController_GetForecast_ManyDays(t *testing.T) {
	forecast := make([]clients.ForecastItem, 14)
	for i := 0; i < 14; i++ {
		day := 17 + i
		forecast[i] = clients.ForecastItem{
			Date:        "2025-02-" + formatDay(day),
			Temperature: decimal.NewFromFloat(float64(10 + i)),
		}
	}

	mock := &mockWeatherClient{
		forecast: forecast,
		err:      nil,
	}
	ctrl := NewCurrentWeatherController(mock)
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	result, err := ctrl.GetForecast(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if len(result.Entries) != 14 {
		t.Fatalf("len(entries) = %d, want 14", len(result.Entries))
	}
}

func TestCurrentWeatherController_GetCurrentWeather_VeryHot(t *testing.T) {
	expectedTemp := decimal.NewFromFloat(50.5)
	mock := &mockWeatherClient{temp: expectedTemp}
	ctrl := NewCurrentWeatherController(mock)

	result, err := ctrl.GetCurrentWeather(
		decimal.NewFromFloat(31.0),
		decimal.NewFromFloat(30.0),
	)

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if !result.Temperature.Equal(expectedTemp) {
		t.Errorf("temperature = %v, want %v", result.Temperature, expectedTemp)
	}
}

// ✅ Тест 8: Очень низкая температура
func TestCurrentWeatherController_GetCurrentWeather_VeryCold(t *testing.T) {
	expectedTemp := decimal.NewFromFloat(-40.0) // -40°C - очень холодно
	mock := &mockWeatherClient{temp: expectedTemp}
	ctrl := NewCurrentWeatherController(mock)

	result, err := ctrl.GetCurrentWeather(
		decimal.NewFromFloat(64.0), // Якутск
		decimal.NewFromFloat(130.0),
	)

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if !result.Temperature.Equal(expectedTemp) {
		t.Errorf("temperature = %v, want %v", result.Temperature, expectedTemp)
	}
}

// Вспомогательная функция для форматирования дня
func formatDay(day int) string {
	if day < 10 {
		return "0" + string(rune(day+'0'))
	}
	return string(rune((day/10)+'0')) + string(rune((day%10)+'0'))
}
