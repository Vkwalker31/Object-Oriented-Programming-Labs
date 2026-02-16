package weather

import "github.com/shopspring/decimal"

type CurrentWeather struct {
	Temperature decimal.Decimal `json:"temperature"`
}

type ForecastEntry struct {
	Date        string          `json:"date"`
	Temperature decimal.Decimal `json:"temperature"`
}

type Forecast struct {
	Entries []ForecastEntry `json:"entries"`
}
