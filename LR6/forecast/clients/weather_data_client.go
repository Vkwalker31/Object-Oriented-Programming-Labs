package clients

import "github.com/shopspring/decimal"

// ForecastItem represents one forecast entry (e.g. one day).
type ForecastItem struct {
	Date        string          `json:"date"`
	Temperature decimal.Decimal `json:"temperature"`
}

type WeatherDataClient interface {
	LocationCurrentTemperature(lat decimal.Decimal, lon decimal.Decimal) (temperature decimal.Decimal, err error)
	LocationForecast(lat decimal.Decimal, lon decimal.Decimal) ([]ForecastItem, error)
}
