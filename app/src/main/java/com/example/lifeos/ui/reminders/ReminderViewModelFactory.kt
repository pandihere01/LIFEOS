
package com.example.lifeos.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifeos.data.repository.ReminderRepository
import com.example.lifeos.service.notification.ReminderScheduler

class ReminderViewModelFactory(
    private val repository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(
                repository,
                reminderScheduler
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
