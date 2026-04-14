package api

import (
	"clients"
	"bytes"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/shopspring/decimal"
)

type mockProvider struct {
	temp     decimal.Decimal
	forecast []clients.ForecastItem
	err      error
}

func (m *mockProvider) LocationCurrentTemperature(lat, lon decimal.Decimal) (decimal.Decimal, error) {
	return m.temp, m.err
}

func (m *mockProvider) LocationForecast(lat, lon decimal.Decimal) ([]clients.ForecastItem, error) {
	return m.forecast, m.err
}

func newTestRouter(h *WeatherHandler) *gin.Engine {
	r := gin.New()
	r.GET("/weather", h.HandleGetCurrentWeather)
	r.GET("/forecast", h.HandleGetForecast)
	r.POST("/weather/batch", h.HandleGetCurrentWeatherBatch)
	return r
}

func TestHandleGetCurrentWeather_WithCity(t *testing.T) {
	gin.SetMode(gin.TestMode)
	mock := &mockProvider{temp: decimal.NewFromFloat(5.5)}
	h := &WeatherHandler{
		Providers: map[string]clients.WeatherDataClient{
			ProviderOpenWeather: mock,
		},
	}
	r := newTestRouter(h)
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
	r := newTestRouter(h)
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
	r := newTestRouter(h)
	req := httptest.NewRequest(http.MethodGet, "/weather?lat=53.9&lon=27.5", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want 200; body: %s", w.Code, w.Body.String())
	}
}

func TestHandleGetCurrentWeather_WithProviderSelection(t *testing.T) {
	gin.SetMode(gin.TestMode)
	open := &mockProvider{temp: decimal.NewFromFloat(1)}
	google := &mockProvider{temp: decimal.NewFromFloat(9)}
	h := &WeatherHandler{
		Providers: map[string]clients.WeatherDataClient{
			ProviderOpenWeather: open,
			ProviderGoogle:      google,
		},
	}
	r := newTestRouter(h)
	req := httptest.NewRequest(http.MethodGet, "/weather?city=Minsk&provider=google", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, want 200; body: %s", w.Code, w.Body.String())
	}
	if !bytes.Contains(w.Body.Bytes(), []byte("9")) {
		t.Errorf("expected google temperature in response, body: %s", w.Body.String())
	}
}

func TestHandleGetForecast_WithCity(t *testing.T) {
	gin.SetMode(gin.TestMode)
	mock := &mockProvider{
		forecast: []clients.ForecastItem{
			{Date: "2026-02-16", Temperature: decimal.NewFromFloat(3.2)},
		},
	}
	h := &WeatherHandler{Providers: map[string]clients.WeatherDataClient{ProviderOpenWeather: mock}}
	r := newTestRouter(h)
	req := httptest.NewRequest(http.MethodGet, "/forecast?city=London", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, want 200; body: %s", w.Code, w.Body.String())
	}
}

func TestHandleGetForecast_InvalidCoordinates(t *testing.T) {
	gin.SetMode(gin.TestMode)
	h := &WeatherHandler{Providers: map[string]clients.WeatherDataClient{ProviderOpenWeather: &mockProvider{}}}
	r := newTestRouter(h)
	req := httptest.NewRequest(http.MethodGet, "/forecast?lat=abc&lon=27.5", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusBadRequest {
		t.Errorf("status = %d, want 400", w.Code)
	}
}

func TestHandleGetCurrentWeatherBatch_Success(t *testing.T) {
	gin.SetMode(gin.TestMode)
	mock := &mockProvider{temp: decimal.NewFromFloat(8.8)}
	h := &WeatherHandler{Providers: map[string]clients.WeatherDataClient{ProviderOpenWeather: mock}}
	r := newTestRouter(h)
	body := `[{"city":"Minsk"},{"lat":"51.5074","lon":"-0.1278"}]`
	req := httptest.NewRequest(http.MethodPost, "/weather/batch", bytes.NewBufferString(body))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, want 200; body: %s", w.Code, w.Body.String())
	}
}

func TestHandleGetCurrentWeatherBatch_BadItem(t *testing.T) {
	gin.SetMode(gin.TestMode)
	h := &WeatherHandler{Providers: map[string]clients.WeatherDataClient{ProviderOpenWeather: &mockProvider{}}}
	r := newTestRouter(h)
	body := `[{"city":"UnknownCity"}]`
	req := httptest.NewRequest(http.MethodPost, "/weather/batch", bytes.NewBufferString(body))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	if w.Code != http.StatusBadRequest {
		t.Errorf("status = %d, want 400; body: %s", w.Code, w.Body.String())
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
