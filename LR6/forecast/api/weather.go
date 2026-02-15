package api

import (
	"clients"
	"controllers"
	weather "models/weather"
	"shared/responses"
	"shared/utils"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/shopspring/decimal"
)

const (
	ProviderOpenWeather = "openweather"
	ProviderGoogle      = "google"
)

type WeatherHandler struct {
	Providers map[string]clients.WeatherDataClient
}

func NewCurrentWeatherHandler() *WeatherHandler {
	openWeatherURL := utils.GetEnv("OPENWEATHER_BASE_URL", "https://api.openweathermap.org/data/2.5/weather")
	googleBaseURL := utils.GetEnv("GOOGLE_WEATHER_BASE_URL", "https://weather.googleapis.com/v1")
	return &WeatherHandler{
		Providers: map[string]clients.WeatherDataClient{
			ProviderOpenWeather: clients.NewOpenWeatherClient(
				utils.GetEnv("OPENWEATHER_API_KEY", ""), openWeatherURL),
			ProviderGoogle: clients.NewGoogleWeatherClient(
				utils.GetEnv("GOOGLE_WEATHER_API_KEY", ""), googleBaseURL),
		},
	}
}

func (h *WeatherHandler) client(provider string) clients.WeatherDataClient {
	if c, ok := h.Providers[provider]; ok {
		return c
	}
	return h.Providers[ProviderOpenWeather]
}

func (h *WeatherHandler) resolveLatLon(c *gin.Context) (lat, lon decimal.Decimal, ok bool) {
	if city := c.Query("city"); city != "" {
		lat, lon, found := ResolveCity(city)
		if !found {
			return decimal.Zero, decimal.Zero, false
		}
		return lat, lon, true
	}
	lat, errLat := decimal.NewFromString(c.Query("lat"))
	lon, errLon := decimal.NewFromString(c.Query("lon"))
	if errLat != nil || errLon != nil {
		return decimal.Zero, decimal.Zero, false
	}
	return lat, lon, true
}

// GetCurrentWeather godoc
// @Summary      Get Current Weather
// @Description  Returns current weather for given coordinates or city name. Use lat+lon or city= (Minsk, London, Tokyo, Shanghai, Warsaw). Provider: openweather (default) or google.
// @Tags         weather
// @Produce      json
// @Param        lat       query     string  false  "Latitude (optional if city is set)"
// @Param        lon       query     string  false  "Longitude (optional if city is set)"
// @Param        city      query     string  false  "City name: Minsk, London, Tokyo, Shanghai, Warsaw"
// @Param        provider  query     string  false  "Provider: openweather or google"  default(openweather)
// @Success      200       {object}  responses.SuccessResponse[weather.CurrentWeather]
// @Failure      400       {object}  responses.StatusResponse
// @Failure      500       {object}  responses.StatusResponse
// @Router       /weather [get]
func (h *WeatherHandler) HandleGetCurrentWeather(c *gin.Context) {
	lat, lon, ok := h.resolveLatLon(c)
	if !ok {
		c.JSON(400, responses.StatusResponse{Code: 400, Message: "invalid coordinates or unsupported city; use lat+lon or city=(Minsk|London|Tokyo|Shanghai|Warsaw)"})
		return
	}
	provider := c.DefaultQuery("provider", ProviderOpenWeather)
	ctrl := controllers.NewCurrentWeatherController(h.client(provider))
	result, err := ctrl.GetCurrentWeather(lat, lon)
	if err != nil {
		c.JSON(500, responses.StatusResponse{Code: 500, Message: err.Error()})
		return
	}
	c.JSON(200, responses.SuccessResponse[weather.CurrentWeather]{Code: 200, Message: "Success", Data: result})
}

// GetForecast godoc
// @Summary      Get Weather Forecast
// @Description  Returns weather forecast for given coordinates or city name
// @Tags         weather
// @Produce      json
// @Param        lat       query     string  false  "Latitude (optional if city is set)"
// @Param        lon       query     string  false  "Longitude (optional if city is set)"
// @Param        city      query     string  false  "City name: Minsk, London, Tokyo, Shanghai, Warsaw"
// @Param        provider  query     string  false  "Provider: openweather or google"  default(openweather)
// @Success      200       {object}  responses.SuccessResponse[weather.Forecast]
// @Failure      400       {object}  responses.StatusResponse
// @Failure      500       {object}  responses.StatusResponse
// @Router       /forecast [get]
func (h *WeatherHandler) HandleGetForecast(c *gin.Context) {
	lat, lon, ok := h.resolveLatLon(c)
	if !ok {
		c.JSON(400, responses.StatusResponse{Code: 400, Message: "invalid coordinates or unsupported city; use lat+lon or city=(Minsk|London|Tokyo|Shanghai|Warsaw)"})
		return
	}
	provider := c.DefaultQuery("provider", ProviderOpenWeather)
	ctrl := controllers.NewCurrentWeatherController(h.client(provider))
	result, err := ctrl.GetForecast(lat, lon)
	if err != nil {
		c.JSON(500, responses.StatusResponse{Code: 500, Message: err.Error()})
		return
	}
	c.JSON(200, responses.SuccessResponse[weather.Forecast]{Code: 200, Message: "Success", Data: result})
}

// LocationRequest is one location for batch request (coordinates or city).
type LocationRequest struct {
	Lat   *string `json:"lat,omitempty"`
	Lon   *string `json:"lon,omitempty"`
	City  *string `json:"city,omitempty"`
}

// LocationWeather is current weather for one location.
type LocationWeather struct {
	Location string                 `json:"location"`
	Weather  weather.CurrentWeather `json:"weather"`
}

// BatchWeatherResponse is the response for batch weather endpoint.
type BatchWeatherResponse []LocationWeather

// GetCurrentWeatherBatch godoc
// @Summary      Get Current Weather for Multiple Locations
// @Description  Returns current temperature for several locations. Each item must have (lat+lon) or city.
// @Tags         weather
// @Accept       json
// @Produce      json
// @Param        body      body      []LocationRequest  true  "List of locations (lat+lon or city per item)"
// @Param        provider  query     string             false  "Provider: openweather or google"  default(openweather)
// @Success      200       {object}  responses.SuccessResponse[BatchWeatherResponse]
// @Failure      400       {object}  responses.StatusResponse
// @Failure      500       {object}  responses.StatusResponse
// @Router       /weather/batch [post]
func (h *WeatherHandler) HandleGetCurrentWeatherBatch(c *gin.Context) {
	var locations []LocationRequest
	if err := c.ShouldBindJSON(&locations); err != nil {
		c.JSON(400, responses.StatusResponse{Code: 400, Message: "invalid JSON body: " + err.Error()})
		return
	}
	provider := c.DefaultQuery("provider", ProviderOpenWeather)
	ctrl := controllers.NewCurrentWeatherController(h.client(provider))
	var results []LocationWeather
	for i, loc := range locations {
		var lat, lon decimal.Decimal
		var label string
		if loc.City != nil && *loc.City != "" {
			var ok bool
			lat, lon, ok = ResolveCity(*loc.City)
			if !ok {
				c.JSON(400, responses.StatusResponse{Code: 400, Message: "unsupported city at index " + strconv.Itoa(i) + ": " + *loc.City})
				return
			}
			label = *loc.City
		} else if loc.Lat != nil && loc.Lon != nil {
			var errLat, errLon error
			lat, errLat = decimal.NewFromString(*loc.Lat)
			lon, errLon = decimal.NewFromString(*loc.Lon)
			if errLat != nil || errLon != nil {
				c.JSON(400, responses.StatusResponse{Code: 400, Message: "invalid coordinates at index " + strconv.Itoa(i)})
				return
			}
			label = *loc.Lat + "," + *loc.Lon
		} else {
			c.JSON(400, responses.StatusResponse{Code: 400, Message: "each location must have (lat+lon) or city"})
			return
		}
		w, err := ctrl.GetCurrentWeather(lat, lon)
		if err != nil {
			c.JSON(500, responses.StatusResponse{Code: 500, Message: err.Error()})
			return
		}
		results = append(results, LocationWeather{Location: label, Weather: w})
	}
	c.JSON(200, responses.SuccessResponse[BatchWeatherResponse]{Code: 200, Message: "Success", Data: results})
}
