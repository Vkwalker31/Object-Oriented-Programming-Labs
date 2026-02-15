package clients

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"

	"github.com/shopspring/decimal"
)

type googleCurrentResponse struct {
	Temperature struct {
		Degrees float64 `json:"degrees"`
		Unit    string  `json:"unit"`
	} `json:"temperature"`
}

type googleForecastDaysResponse struct {
	DailyForecasts []struct {
		DisplayDate string `json:"displayDate"`
		DaytimeForecast *struct {
			Temperature *struct {
				Degrees float64 `json:"degrees"`
			} `json:"temperature"`
		} `json:"daytimeForecast"`
	} `json:"dailyForecasts"`
}

// GoogleWeatherClient implements WeatherDataClient for Google Weather API.
type GoogleWeatherClient struct {
	apiKey  string
	baseURL string
	client  *http.Client
}

func NewGoogleWeatherClient(apiKey string, baseURL string) *GoogleWeatherClient {
	return &GoogleWeatherClient{
		apiKey:  apiKey,
		baseURL: baseURL,
		client:  http.DefaultClient,
	}
}

func (g *GoogleWeatherClient) LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (decimal.Decimal, error) {
	path := strings.TrimSuffix(g.baseURL, "/") + "/currentConditions:lookup"
	url := fmt.Sprintf("%s?key=%s&location.latitude=%s&location.longitude=%s",
		path, g.apiKey, lat.String(), lon.String())

	resp, err := g.client.Get(url)
	if err != nil {
		return decimal.Zero, fmt.Errorf("failed to call google weather: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return decimal.Zero, fmt.Errorf("google weather returned bad status: %d", resp.StatusCode)
	}

	var data googleCurrentResponse
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return decimal.Zero, fmt.Errorf("failed to decode google response: %w", err)
	}

	return decimal.NewFromFloat(data.Temperature.Degrees), nil
}

func (g *GoogleWeatherClient) LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]ForecastItem, error) {
	path := strings.TrimSuffix(g.baseURL, "/") + "/forecast/days:lookup"
	url := fmt.Sprintf("%s?key=%s&location.latitude=%s&location.longitude=%s",
		path, g.apiKey, lat.String(), lon.String())

	resp, err := g.client.Get(url)
	if err != nil {
		return nil, fmt.Errorf("failed to call google forecast: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("google forecast returned bad status: %d", resp.StatusCode)
	}

	var data googleForecastDaysResponse
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return nil, fmt.Errorf("failed to decode google forecast: %w", err)
	}

	var result []ForecastItem
	for _, d := range data.DailyForecasts {
		temp := decimal.Zero
		if d.DaytimeForecast != nil && d.DaytimeForecast.Temperature != nil {
			temp = decimal.NewFromFloat(d.DaytimeForecast.Temperature.Degrees)
		}
		result = append(result, ForecastItem{Date: d.DisplayDate, Temperature: temp})
	}
	return result, nil
}
