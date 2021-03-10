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

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CountDownViewModel : ViewModel() {

    var countDownTimer: CountDownTimer? = null

    var startTimerFromMillis: Long = 0
        private set

    private var millisUntilFinished: Long = 0

    private val _timerRunningLiveData = MutableLiveData<Boolean>()
    val isTimerRunningLiveData: LiveData<Boolean> = _timerRunningLiveData

    private val _remainingTimeLiveData = MutableLiveData<String>()
    val remainingTimeLiveData: LiveData<String> = _remainingTimeLiveData

    private val _timerCompletionPercentageLiveData = MutableLiveData<Float>()
    val timerCompletionPercentageLiveData: LiveData<Float> = _timerCompletionPercentageLiveData

    fun setStartTimerFrom(millis: Long) {
        startTimerFromMillis = millis
        millisUntilFinished = millis
        showRemainingTime(millis)
    }

    fun startCountDown(millis: Long = millisUntilFinished, interval: Long = 1000L) {
        countDownTimer = object : CountDownTimer(millis, interval) {
            override fun onTick(millisUntilFinished: Long) {
                this@CountDownViewModel.millisUntilFinished = millisUntilFinished
                _timerRunningLiveData.value = true
                showRemainingTime(millisUntilFinished)
                showTimerCompletedPercentage(startTimerFromMillis, millisUntilFinished)
            }

            override fun onFinish() {
                _timerRunningLiveData.value = false
                showRemainingTime(0)
                _timerCompletionPercentageLiveData.value = 1f
            }
        }
        countDownTimer?.start()
        _timerRunningLiveData.value = true
    }

    fun pauseCountDown() {
        countDownTimer?.cancel()
        _timerRunningLiveData.value = false
    }

    private fun showRemainingTime(millis: Long) {
        val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.ROOT)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val givenTime = Date(millis)
        val formattedTime = simpleDateFormat.format(givenTime)
        _remainingTimeLiveData.value = formattedTime
    }

    private fun showTimerCompletedPercentage(totalTime: Long, millisUntilFinished: Long) {
        val diff = totalTime - millisUntilFinished
        val percent: Float = diff.toFloat() / totalTime
        _timerCompletionPercentageLiveData.value = percent
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
