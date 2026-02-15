package controllers

import (
	"errors"
	"testing"

	"clients"
	weather "models/weather"

	"github.com/shopspring/decimal"
)

type mockWeatherClient struct {
	temp     decimal.Decimal
	err      error
	forecast []struct {
		date string
		temp decimal.Decimal
	}
}

func (m *mockWeatherClient) LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (decimal.Decimal, error) {
	return m.temp, m.err
}

func (m *mockWeatherClient) LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]clients.ForecastItem, error) {
	if m.err != nil {
		return nil, m.err
	}
	out := make([]clients.ForecastItem, len(m.forecast))
	for i, f := range m.forecast {
		out[i] = clients.ForecastItem{Date: f.date, Temperature: f.temp}
	}
	return out, nil
}

func TestCurrentWeatherController_GetCurrentWeather_Success(t *testing.T) {
	expectedTemp := decimal.NewFromFloat(22.5)
	mock := &mockWeatherClient{temp: expectedTemp, err: nil}
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
	mock := &mockWeatherClient{temp: decimal.Zero, err: clientErr}
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
		forecast: []struct {
			date string
			temp decimal.Decimal
		}{
			{"2025-02-17", decimal.NewFromFloat(10)},
			{"2025-02-18", decimal.NewFromFloat(12)},
		},
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
	if result.Entries[0].Date != "2025-02-17" || !result.Entries[0].Temperature.Equal(decimal.NewFromFloat(10)) {
		t.Errorf("first entry = %+v", result.Entries[0])
	}
}
