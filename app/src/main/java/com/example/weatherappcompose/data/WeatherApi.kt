package com.example.weatherappcompose.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

const val API_KEY = "03f958e0dac74fe6a64182126240402"

class WeatherApi {

    fun getData(
        city: String,
        context: Context,
        daysList: MutableState<List<WeatherModel>>,
        currentDay: MutableState<WeatherModel>
    ) {
        val queue = Volley.newRequestQueue(context)

        val sRequest = StringRequest(
            Request.Method.GET,
            getUrl(city),
            { response ->
                val list = getWeatherByDays(response)

                currentDay.value = list[0]
                daysList.value = list
            },
            {
                Log.e("MyLog", "VolleyError: $it")
            }
        )

        queue.add(sRequest)
    }


    private fun getUrl(city: String): String {
        return "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
                "&q=$city" +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no";
    }

    private fun getWeatherByDays(response: String): List<WeatherModel> {
        if (response.isEmpty()) {
            return listOf()
        }

        val list = ArrayList<WeatherModel>()

        val mainObject = JSONObject(response)
        val city = mainObject.getJSONObject("location").getString("name")
        val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

        for (i in 0 until days.length()) {
            val item = days[i] as JSONObject

            list.add(
                WeatherModel(
                    city,
                    item.getString("date"),
                    "",
                    item.getJSONObject("day").getJSONObject("condition")
                        .getString("text"),
                    item.getJSONObject("day").getJSONObject("condition")
                        .getString("icon"),
                    item.getJSONObject("day").getString("maxtemp_c"),
                    item.getJSONObject("day").getString("mintemp_c"),
                    item.getJSONArray("hour").toString()
                )
            )
        }

        list[0] = list[0].copy(
            time = mainObject.getJSONObject("current").getString("last_updated"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c")
        )


        return list
    }


    fun getWeatherByHours(hours: String): List<WeatherModel> {
        if (hours.isEmpty()) return listOf()

        val hoursArray = JSONArray(hours)
        val list = ArrayList<WeatherModel>()

        for (i in 0 until hoursArray.length()) {
            val item = hoursArray[i] as JSONObject
            list.add(
                WeatherModel(
                    "",
                    item.getString("time"),
                    item.getString("temp_c").toFloat().toInt().toString() + "°C",
                    item.getJSONObject("condition").getString("text"),
                    item.getJSONObject("condition").getString("icon"),
                    "",
                    "",
                    ""
                )
            )
        }

        return list
    }
}