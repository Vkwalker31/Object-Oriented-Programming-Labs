package api

import (
	"strings"

	"github.com/shopspring/decimal"
)

// Supported city names (case-insensitive): Minsk, London, Tokyo, Shanghai, Warsaw.
var cityCoordinates = map[string]struct{ lat, lon string }{
	"minsk":    {"53.9045", "27.5615"},
	"london":   {"51.5074", "-0.1278"},
	"tokyo":    {"35.6762", "139.6503"},
	"shanghai": {"31.2304", "121.4737"},
	"warsaw":   {"52.2297", "21.0122"},
}

// ResolveCity returns (lat, lon, true) for a supported city name, or (zero, zero, false) if unknown.
func ResolveCity(city string) (decimal.Decimal, decimal.Decimal, bool) {
	key := strings.TrimSpace(strings.ToLower(city))
	coord, ok := cityCoordinates[key]
	if !ok {
		return decimal.Zero, decimal.Zero, false
	}
	lat, _ := decimal.NewFromString(coord.lat)
	lon, _ := decimal.NewFromString(coord.lon)
	return lat, lon, true
}
