package com.lifetracker.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifetracker.data.AppDatabase
import com.lifetracker.data.CheckIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class CheckInViewModel(private val db: AppDatabase) : ViewModel() {

    private val _habits = MutableStateFlow<List<String>>(emptyList())
    val habits: StateFlow<List<String>> = _habits.asStateFlow()

    private val _selectedHabit = MutableStateFlow("")
    val selectedHabit: StateFlow<String> = _selectedHabit.asStateFlow()

    private val _checkIns = MutableStateFlow<List<CheckIn>>(emptyList())
    val checkIns: StateFlow<List<CheckIn>> = _checkIns.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _newHabitName = MutableStateFlow("")
    val newHabitName: StateFlow<String> = _newHabitName.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            db.checkInDao().getHabitNames().collect { list ->
                _habits.value = list
                if (list.isNotEmpty() && _selectedHabit.value.isEmpty()) {
                    _selectedHabit.value = list.first()
                }
                if (list.isNotEmpty()) loadCheckIns(list.first())
            }
        }
    }

    fun selectHabit(name: String) {
        _selectedHabit.value = name
        loadCheckIns(name)
    }

    private fun loadCheckIns(name: String) {
        viewModelScope.launch {
            db.checkInDao().getByHabit(name).collect { list ->
                _checkIns.value = list
            }
        }
    }

    fun checkIn(name: String) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val alreadyChecked = _checkIns.value.any {
                it.checkDate.toLocalDate() == today
            }
            if (!alreadyChecked) {
                db.checkInDao().insert(CheckIn(habitName = name, checkDate = LocalDateTime.now()))
            }
        }
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun showAddDialog() { _showAddDialog.value = true }
    fun dismissAddDialog() { _showAddDialog.value = false }
    fun onNewHabitNameChange(text: String) { _newHabitName.value = text }

    fun addHabit() {
        val name = _newHabitName.value.trim()
        if (name.isNotEmpty()) {
            viewModelScope.launch {
                db.checkInDao().insert(CheckIn(habitName = name, checkDate = LocalDateTime.now()))
                _newHabitName.value = ""
                dismissAddDialog()
                loadHabits()
            }
        }
    }
}
