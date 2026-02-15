package controllers

import (
	"clients"
	weather "models/weather"

	"github.com/shopspring/decimal"
)

type CurrentWeatherController struct {
	Client clients.WeatherDataClient
}

func NewCurrentWeatherController(client clients.WeatherDataClient) *CurrentWeatherController {
	return &CurrentWeatherController{Client: client}
}

func (c *CurrentWeatherController) GetCurrentWeather(lat decimal.Decimal, lon decimal.Decimal) (weather.CurrentWeather, error) {
	temperature, err := c.Client.LocationCurrentTemperature(lat, lon)
	if err != nil {
		return weather.CurrentWeather{}, err
	}
	return weather.CurrentWeather{Temperature: temperature}, nil
}

func (c *CurrentWeatherController) GetForecast(lat decimal.Decimal, lon decimal.Decimal) (weather.Forecast, error) {
	items, err := c.Client.LocationForecast(lat, lon)
	if err != nil {
		return weather.Forecast{}, err
	}
	entries := make([]weather.ForecastEntry, len(items))
	for i, it := range items {
		entries[i] = weather.ForecastEntry{Date: it.Date, Temperature: it.Temperature}
	}
	return weather.Forecast{Entries: entries}, nil
}
