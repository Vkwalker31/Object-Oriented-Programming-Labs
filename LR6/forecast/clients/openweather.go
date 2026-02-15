package clients

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"

	"github.com/shopspring/decimal"
)

type openWeatherResponse struct {
	Main struct {
		Temp decimal.Decimal `json:"temp"`
	} `json:"main"`
}

type openWeatherForecastResponse struct {
	List []struct {
		DtTxt string `json:"dt_txt"`
		Main  struct {
			Temp decimal.Decimal `json:"temp"`
		} `json:"main"`
	} `json:"list"`
}

type OpenWeatherClient struct {
	apiKey  string
	baseURL string
}

func NewOpenWeatherClient(apiKey string, baseURL string) *OpenWeatherClient {
	return &OpenWeatherClient{
		apiKey:  apiKey,
		baseURL: baseURL,
	}
}

// Implementation of WeatherDataClient
func (c *OpenWeatherClient) LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (decimal.Decimal, error) {
	url := fmt.Sprintf("%s?lat=%s&lon=%s&appid=%s&units=metric",
		c.baseURL, lat.String(), lon.String(), c.apiKey)

	resp, err := http.Get(url)
	if err != nil {
		return decimal.Zero, fmt.Errorf("failed to call openweather: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return decimal.Zero, fmt.Errorf("openweather returned bad status: %d", resp.StatusCode)
	}

	var data openWeatherResponse
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return decimal.Zero, fmt.Errorf("failed to decode response: %w", err)
	}

	return data.Main.Temp, nil
}

func (c *OpenWeatherClient) LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]ForecastItem, error) {
	forecastBase := c.baseURL
	if strings.Contains(c.baseURL, "weather") {
		forecastBase = strings.Replace(c.baseURL, "weather", "forecast", 1)
	} else {
		forecastBase = strings.TrimSuffix(c.baseURL, "/") + "/forecast"
	}
	url := fmt.Sprintf("%s?lat=%s&lon=%s&appid=%s&units=metric",
		forecastBase, lat.String(), lon.String(), c.apiKey)

	resp, err := http.Get(url)
	if err != nil {
		return nil, fmt.Errorf("failed to call openweather forecast: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("openweather forecast returned bad status: %d", resp.StatusCode)
	}

	var data openWeatherForecastResponse
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return nil, fmt.Errorf("failed to decode forecast response: %w", err)
	}

	// One entry per day (take first 3h block per day)
	seen := make(map[string]bool)
	var result []ForecastItem
	for _, item := range data.List {
		date := item.DtTxt
		if len(date) >= 10 {
			date = date[:10] // "2025-01-28 12:00:00" -> "2025-01-28"
		}
		if seen[date] {
			continue
		}
		seen[date] = true
		result = append(result, ForecastItem{Date: date, Temperature: item.Main.Temp})
	}
	return result, nil
}
