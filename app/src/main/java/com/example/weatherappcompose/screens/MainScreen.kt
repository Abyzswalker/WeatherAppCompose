package com.example.weatherappcompose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherappcompose.R
import com.example.weatherappcompose.data.WeatherApi
import com.example.weatherappcompose.data.WeatherModel
import com.example.weatherappcompose.ui.theme.GreenMain
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

//@Preview(showBackground = true)
@Composable
fun MainCard(currentDay: MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(GreenMain),
            //Тень
            elevation = CardDefaults.cardElevation(5.dp),
            //Закругление
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 9.dp, start = 9.dp),
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:" + currentDay.value.icon,
                        contentDescription = "img2",
                        modifier = Modifier
                            .padding(top = 3.dp, end = 8.dp)
                            .size(35.dp)
                    )

                }

                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )

                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() + "°C"
                    else "${currentDay.value.maxTemp.toFloat().toInt()}°C" +
                            "/${currentDay.value.minTemp.toFloat().toInt()}°C",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )

                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            onClickSearch.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "iconImg",
                            tint = Color.White
                        )

                    }

                    Text(
                        text = "${currentDay.value.maxTemp}°C/${currentDay.value.minTemp}°C",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            onClickSync.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "iconImg2",
                            tint = Color.White
                        )

                    }

                }
            }

        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val weatherApi = WeatherApi()
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState (pageCount = { 3 })
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(start = 3.dp, end = 3.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = GreenMain,
            contentColor = Color.White,
            indicator = { pos ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(pos[tabIndex])
                )
            }
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }

                    },
                    text = {
                        Text(text = text, color = Color.White)
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1.0f)
            //beyondBoundsPageCount = 3
        ) { index: Int ->
            val list = when(index) {
                0 -> weatherApi.getWeatherByHours(currentDay.value.hours)
                //1 -> daysList.value

                else -> daysList.value
            }

            MainList(list, currentDay)
        }
    }
}
