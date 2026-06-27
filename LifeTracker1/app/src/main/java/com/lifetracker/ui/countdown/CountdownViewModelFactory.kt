package com.lifetracker.ui.countdown

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifetracker.data.AppDatabase

class CountdownViewModelFactory(
    private val app: Application,
    private val db: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountdownViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CountdownViewModel(app, db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
