package clients

import "github.com/shopspring/decimal"

type ForecastItem struct {
	Date        string          `json:"date"`
	Temperature decimal.Decimal `json:"temperature"`
}

type WeatherDataClient interface {
	LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (temperature decimal.Decimal, err error)
	LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]ForecastItem, error)
}
