
package com.example.lifeos.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifeos.data.repository.CalendarRepository

class CalendarViewModelFactory(
    private val repository: CalendarRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
