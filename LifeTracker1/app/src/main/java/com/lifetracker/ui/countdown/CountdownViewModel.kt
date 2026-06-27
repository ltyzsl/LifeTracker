package com.lifetracker.ui.countdown

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lifetracker.alarm.AlarmScheduler
import com.lifetracker.data.AppDatabase
import com.lifetracker.data.Countdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CountdownViewModel(private val app: Application, private val db: AppDatabase) :
    AndroidViewModel(app) {

    private val _countdowns = MutableStateFlow<List<Countdown>>(emptyList())
    val countdowns: StateFlow<List<Countdown>> = _countdowns.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _titleInput = MutableStateFlow("")
    val titleInput: StateFlow<String> = _titleInput.asStateFlow()

    private val _selectedDateTime = MutableStateFlow(LocalDateTime.now().plusHours(1))
    val selectedDateTime: StateFlow<LocalDateTime> = _selectedDateTime.asStateFlow()

    init {
        viewModelScope.launch {
            db.countdownDao().getActive().collect { list ->
                _countdowns.value = list
            }
        }
    }

    fun onTitleChange(text: String) { _titleInput.value = text }
    fun onDateTimeChange(dt: LocalDateTime) { _selectedDateTime.value = dt }
    fun showDialog() { _showDialog.value = true }
    fun dismissDialog() { _showDialog.value = false }

    fun addCountdown() {
        val title = _titleInput.value.trim()
        if (title.isEmpty()) return
        viewModelScope.launch {
            val id = db.countdownDao().insert(
                Countdown(title = title, targetTime = _selectedDateTime.value)
            )
            AlarmScheduler.scheduleCountdown(
                app.applicationContext,
                id,
                title,
                _selectedDateTime.value
            )
            dismissDialog()
        }
    }

    fun deleteCountdown(c: Countdown) {
        viewModelScope.launch {
            db.countdownDao().delete(c)
            AlarmScheduler.cancelCountdown(app.applicationContext, c.id)
        }
    }
}
