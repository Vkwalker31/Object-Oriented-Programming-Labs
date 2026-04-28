package clients

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
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
		DisplayDate     string `json:"displayDate"`
		DaytimeForecast *struct {
			Temperature *struct {
				Degrees float64 `json:"degrees"`
			} `json:"temperature"`
		} `json:"daytimeForecast"`
	} `json:"dailyForecasts"`
}

type GoogleWeatherClient struct {
	apiKey  string
	baseURL string
	client  *http.Client
}

func NewGoogleWeatherClient(apiKey string, baseURL string) *GoogleWeatherClient {
	return &GoogleWeatherClient{
		apiKey:  apiKey,
		baseURL: strings.TrimSuffix(baseURL, "/"),
		client:  http.DefaultClient,
	}
}

func (g *GoogleWeatherClient) fixCoords(lat, lon decimal.Decimal) (decimal.Decimal, decimal.Decimal) {
	if lat.Abs().GreaterThan(decimal.NewFromInt(90)) {
		fmt.Printf("[FIX] Swapping Lat/Lon. Original: Lat=%s, Lon=%s\n", lat.String(), lon.String())
		return lon, lat
	}
	return lat, lon
}

func (g *GoogleWeatherClient) LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (decimal.Decimal, error) {
	lat, lon = g.fixCoords(lat, lon)

	u := fmt.Sprintf("%s/currentConditions:lookup", g.baseURL)

	q := url.Values{}
	q.Set("key", g.apiKey)
	q.Set("location.latitude", lat.String())
	q.Set("location.longitude", lon.String())

	fullURL := u + "?" + q.Encode()
	fmt.Println("[DEBUG] Calling Google Current:", fullURL)

	resp, err := g.doGet(fullURL)
	if err != nil {
		return decimal.Zero, err
	}
	defer resp.Body.Close()

	var data googleCurrentResponse
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return decimal.Zero, fmt.Errorf("decode error: %w", err)
	}

	return decimal.NewFromFloat(data.Temperature.Degrees), nil
}

func (g *GoogleWeatherClient) LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]ForecastItem, error) {
	lat, lon = g.fixCoords(lat, lon)

	u := fmt.Sprintf("%s/forecast:lookup", g.baseURL)

	q := url.Values{}
	q.Set("key", g.apiKey)
	q.Set("location.latitude", lat.String())
	q.Set("location.longitude", lon.String())

	fullURL := u + "?" + q.Encode()
	fmt.Println("[DEBUG] Calling Google Forecast:", fullURL)

	resp, err := g.doGet(fullURL)
	if err != nil {
		fmt.Println("[DEBUG] Primary forecast failed, trying alternative path...")
		u = fmt.Sprintf("%s/forecast/days:lookup", g.baseURL)
		fullURL = u + "?" + q.Encode()
		resp, err = g.doGet(fullURL)
		if err != nil {
			return nil, err
		}
	}
	defer resp.Body.Close()

	var data googleForecastDaysResponse
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return nil, fmt.Errorf("decode error: %w", err)
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

func (g *GoogleWeatherClient) doGet(requestURL string) (*http.Response, error) {
	req, err := http.NewRequest(http.MethodGet, requestURL, nil)
	if err != nil {
		return nil, err
	}

	req.Header.Set("X-Goog-Api-Key", g.apiKey)
	resp, err := g.client.Do(req)
	if err != nil {
		return nil, err
	}

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		resp.Body.Close()
		return nil, fmt.Errorf("google api error (status %d): %s", resp.StatusCode, string(body))
	}

	return resp, nil
}
