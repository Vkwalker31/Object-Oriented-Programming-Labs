package api

import (
	"clients"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/shopspring/decimal"
)

type mockProvider struct {
	temp    decimal.Decimal
	forecast []clients.ForecastItem
	err     error
}

func (m *mockProvider) LocationCurrentTemperature(lat, lon decimal.Decimal) (decimal.Decimal, error) {
	return m.temp, m.err
}

func (m *mockProvider) LocationForecast(lat, lon decimal.Decimal) ([]clients.ForecastItem, error) {
	return m.forecast, m.err
}

func TestHandleGetCurrentWeather_WithCity(t *testing.T) {
	gin.SetMode(gin.TestMode)
	mock := &mockProvider{temp: decimal.NewFromFloat(5.5)}
	h := &WeatherHandler{
		Providers: map[string]clients.WeatherDataClient{
			ProviderOpenWeather: mock,
		},
	}
	r := gin.New()
	r.GET("/weather", h.HandleGetCurrentWeather)
	req := httptest.NewRequest(http.MethodGet, "/weather?city=Minsk", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want 200; body: %s", w.Code, w.Body.String())
	}
}

func TestHandleGetCurrentWeather_InvalidCity(t *testing.T) {
	gin.SetMode(gin.TestMode)
	h := &WeatherHandler{Providers: map[string]clients.WeatherDataClient{"openweather": &mockProvider{}}}
	r := gin.New()
	r.GET("/weather", h.HandleGetCurrentWeather)
	req := httptest.NewRequest(http.MethodGet, "/weather?city=UnknownCity", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusBadRequest {
		t.Errorf("status = %d, want 400", w.Code)
	}
}

func TestHandleGetCurrentWeather_WithCoordinates(t *testing.T) {
	gin.SetMode(gin.TestMode)
	mock := &mockProvider{temp: decimal.NewFromFloat(10)}
	h := &WeatherHandler{
		Providers: map[string]clients.WeatherDataClient{ProviderOpenWeather: mock},
	}
	r := gin.New()
	r.GET("/weather", h.HandleGetCurrentWeather)
	req := httptest.NewRequest(http.MethodGet, "/weather?lat=53.9&lon=27.5", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want 200; body: %s", w.Code, w.Body.String())
	}
}

func TestResolveCity_Supported(t *testing.T) {
	cities := []string{"Minsk", "London", "Tokyo", "Shanghai", "Warsaw"}
	for _, name := range cities {
		lat, lon, ok := ResolveCity(name)
		if !ok {
			t.Errorf("ResolveCity(%q) ok = false", name)
		}
		if lat.IsZero() && lon.IsZero() {
			t.Errorf("ResolveCity(%q) returned zero coords", name)
		}
	}
}

func TestResolveCity_Unsupported(t *testing.T) {
	_, _, ok := ResolveCity("UnknownCity")
	if ok {
		t.Error("expected ok = false for unknown city")
	}
}
