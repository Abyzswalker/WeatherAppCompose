package com.example.weatherappcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.weatherappcompose.data.WeatherApi
import com.example.weatherappcompose.data.WeatherModel
import com.example.weatherappcompose.screens.DialogSearch
import com.example.weatherappcompose.screens.MainCard
import com.example.weatherappcompose.screens.TabLayout


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Image(
                painter = painterResource(R.drawable.weather_bg),
                "img",
                modifier = Modifier.fillMaxSize(),
                //.alpha(0.5f),
                contentScale = ContentScale.FillBounds
            )

            MainScreen(this)
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    val location = remember { mutableStateOf("") }
    val isCheckPermission = remember { mutableStateOf(false) }

    CheckPermission(location, context)

    val weatherApi = WeatherApi()

    val daysList = remember {
        mutableStateOf(listOf<WeatherModel>())
    }

    val dialogState = remember {
        mutableStateOf(false)
    }

    val currentDay = remember {
        mutableStateOf(
            WeatherModel(
                "",
                "",
                "0",
                "",
                "",
                "0",
                "0",
                ""
            )
        )
    }

    if (dialogState.value) {
        DialogSearch(dialogState, onSubmit = {
            weatherApi.getData(it, context, daysList, currentDay)
        })
    }

    if (isCheckPermission.value) {
        CheckPermission(location, context)
    }

    Column {
        MainCard(currentDay,
            onClickSync = {
                if (location.value.isNotEmpty()) {
                    weatherApi.getData(location.value, context, daysList, currentDay)
                } else  {
                    isCheckPermission.value = true
                }
            },
            onClickSearch = {
                dialogState.value = true
            }
        )
        TabLayout(daysList, currentDay)
    }
}
