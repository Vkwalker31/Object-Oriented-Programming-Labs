package clients

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/shopspring/decimal"
)

func TestGoogleWeatherClient_LocationCurrentTemperature_Success(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"temperature":{"degrees":20.5,"unit":"CELSIUS"}}`))
	}))
	defer server.Close()

	client := NewGoogleWeatherClient("test-key", server.URL)
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	temp, err := client.LocationCurrentTemperature(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if !temp.Equal(decimal.NewFromFloat(20.5)) {
		t.Errorf("temperature = %v, want 20.5", temp)
	}
}

func TestGoogleWeatherClient_LocationCurrentTemperature_BadStatus(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.WriteHeader(http.StatusForbidden)
	}))
	defer server.Close()

	client := NewGoogleWeatherClient("test-key", server.URL)
	_, err := client.LocationCurrentTemperature(decimal.Zero, decimal.Zero)
	if err == nil {
		t.Fatal("expected error for 403 status")
	}
}

func TestGoogleWeatherClient_LocationForecast_Success(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"dailyForecasts":[
			{"displayDate":"2025-02-17","daytimeForecast":{"temperature":{"degrees":10}}},
			{"displayDate":"2025-02-18","daytimeForecast":{"temperature":{"degrees":12}}}
		]}`))
	}))
	defer server.Close()

	client := NewGoogleWeatherClient("test-key", server.URL)
	items, err := client.LocationForecast(decimal.NewFromFloat(53.9), decimal.NewFromFloat(27.5))
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if len(items) != 2 {
		t.Fatalf("len(items) = %d, want 2", len(items))
	}
	if items[0].Date != "2025-02-17" || !items[0].Temperature.Equal(decimal.NewFromFloat(10)) {
		t.Errorf("first = %+v", items[0])
	}
}
