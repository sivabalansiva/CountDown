/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private val countDownVIewModel by lazy {
        ViewModelProvider(this).get(CountDownViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }

    // Start building your app here!
    @Composable
    fun MyApp() {

        val timeRemaining by countDownVIewModel.remainingTimeLiveData.observeAsState("00:00")
        val timerCompletionPercentage by countDownVIewModel.timerCompletionPercentageLiveData.observeAsState(
            0f
        )
        val isTimerRunning by countDownVIewModel.isTimerRunningLiveData.observeAsState(false)

        Surface(color = MaterialTheme.colors.surface) {
            Column(
                modifier = Modifier,
            ) {
                TopAppBar {
                    Text(
                        text = "CountDown Timer",
                        style = MaterialTheme.typography.h6,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                TimerUiComposable(timeRemaining, timerCompletionPercentage, isTimerRunning)
            }
        }
    }

    private fun onTimerStartStopClicked(isTimerRunning: Boolean) {
        if (isTimerRunning) {
            countDownVIewModel.pauseCountDown()
        } else {
            countDownVIewModel.startCountDown()
        }
    }

    @Composable
    fun TimerUiComposable(
        timeRemaining: String,
        timerCompletionPercentage: Float,
        isTimerRunning: Boolean
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.background(Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    val progress = if (timerCompletionPercentage.isNaN()) 0f else timerCompletionPercentage
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(width = 240.dp, height = 240.dp),
                        strokeWidth = 4.dp
                    )
                    Text(text = timeRemaining, style = MaterialTheme.typography.h3)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 16.dp, y = 64.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { onTimerStartStopClicked(isTimerRunning) }) {
                    if (isTimerRunning) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "Stop",
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.White
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        items(10, null) { index: Int ->
                            val millis: Long = (index * 30000) + 30000L
                            val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.ROOT)
                            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val millisInstant = Date(millis)
                            val timeString = simpleDateFormat.format(millisInstant)

                            Box(
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    .clickable(!isTimerRunning) {
                                        countDownVIewModel.setStartTimerFrom(millis)
                                    }
                            ) {
                                Text(
                                    text = timeString,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    //region preview region
    @Preview("Light Theme", widthDp = 360, heightDp = 640)
    @Composable
    fun LightPreview() {
        MyTheme {
            MyApp()
        }
    }

    @Preview("Dark Theme", widthDp = 360, heightDp = 640)
    @Composable
    fun DarkPreview() {
        MyTheme(darkTheme = true) {
            MyApp()
        }
    }
    // endregion
}
