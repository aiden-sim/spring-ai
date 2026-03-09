package com.example.mcpserver.tool

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component

@Component
class WeatherTools {

    @Tool(description = "Get current weather information for a given city")
    fun getCurrentWeather(
        @ToolParam(description = "The name of the city to get weather for") city: String
    ): String {
        val weatherData = mapOf(
            "seoul"    to "Seoul: 15°C, Partly Cloudy, Humidity 60%",
            "busan"    to "Busan: 18°C, Sunny, Humidity 55%",
            "jeju"     to "Jeju: 20°C, Windy, Humidity 70%",
            "new york" to "New York: 10°C, Rainy, Humidity 80%",
            "tokyo"    to "Tokyo: 17°C, Clear, Humidity 50%",
            "london"   to "London: 8°C, Foggy, Humidity 85%",
        )
        return weatherData[city.lowercase()]
            ?: "Weather data not available for '$city'. Available cities: Seoul, Busan, Jeju, New York, Tokyo, London"
    }

    @Tool(description = "Get weather forecast for the next N days for a given city")
    fun getWeatherForecast(
        @ToolParam(description = "The name of the city") city: String,
        @ToolParam(description = "Number of days for the forecast (1-7)") days: Int
    ): String {
        val cappedDays = days.coerceIn(1, 7)
        val conditions = listOf("Sunny", "Cloudy", "Rainy", "Partly Cloudy", "Clear")
        val forecasts = (1..cappedDays).map { day ->
            val temp = (10..25).random()
            "Day $day: ${temp}°C, ${conditions.random()}"
        }
        return "$city ${cappedDays}-day forecast:\n" + forecasts.joinToString("\n")
    }
}

