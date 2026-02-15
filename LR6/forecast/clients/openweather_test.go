package clients

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/shopspring/decimal"
)

func TestOpenWeatherClient_LocationCurrentTemperature_Success(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/" && r.URL.Path != "" {
			t.Errorf("unexpected path: %s", r.URL.Path)
		}
		if r.URL.Query().Get("appid") == "" {
			http.Error(w, "missing appid", http.StatusUnauthorized)
			return
		}
		resp := openWeatherResponse{}
		resp.Main.Temp = decimal.NewFromFloat(15.5)
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(resp)
	}))
	defer server.Close()

	client := NewOpenWeatherClient("test-key", server.URL)
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	temp, err := client.LocationCurrentTemperature(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if !temp.Equal(decimal.NewFromFloat(15.5)) {
		t.Errorf("temperature = %v, want 15.5", temp)
	}
}

func TestOpenWeatherClient_LocationCurrentTemperature_BadStatus(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.WriteHeader(http.StatusInternalServerError)
	}))
	defer server.Close()

	client := NewOpenWeatherClient("test-key", server.URL)
	lat := decimal.NewFromFloat(0)
	lon := decimal.NewFromFloat(0)

	_, err := client.LocationCurrentTemperature(lat, lon)
	if err == nil {
		t.Fatal("expected error for 500 status")
	}
}

func TestOpenWeatherClient_LocationCurrentTemperature_InvalidJSON(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte("not json"))
	}))
	defer server.Close()

	client := NewOpenWeatherClient("test-key", server.URL)
	lat := decimal.NewFromFloat(0)
	lon := decimal.NewFromFloat(0)

	_, err := client.LocationCurrentTemperature(lat, lon)
	if err == nil {
		t.Fatal("expected error for invalid JSON")
	}
}

func TestOpenWeatherClient_LocationCurrentTemperature_NetworkError(t *testing.T) {
	client := NewOpenWeatherClient("test-key", "http://nonexistent.invalid.example")
	lat := decimal.NewFromFloat(0)
	lon := decimal.NewFromFloat(0)

	_, err := client.LocationCurrentTemperature(lat, lon)
	if err == nil {
		t.Fatal("expected error for network failure")
	}
}

func TestOpenWeatherClient_LocationForecast_Success(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"list":[
			{"dt_txt":"2025-02-17 12:00:00","main":{"temp":11}},
			{"dt_txt":"2025-02-17 15:00:00","main":{"temp":12}},
			{"dt_txt":"2025-02-18 12:00:00","main":{"temp":10}}
		]}`))
	}))
	defer server.Close()

	client := NewOpenWeatherClient("test-key", server.URL+"/weather")
	lat := decimal.NewFromFloat(53.9)
	lon := decimal.NewFromFloat(27.5)

	items, err := client.LocationForecast(lat, lon)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if len(items) != 2 {
		t.Fatalf("len(items) = %d, want 2 (one per day)", len(items))
	}
	if items[0].Date != "2025-02-17" || !items[0].Temperature.Equal(decimal.NewFromFloat(11)) {
		t.Errorf("first day = %+v", items[0])
	}
	if items[1].Date != "2025-02-18" || !items[1].Temperature.Equal(decimal.NewFromFloat(10)) {
		t.Errorf("second day = %+v", items[1])
	}
}
