
package com.example.lifeos.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeos.data.local.entity.ReminderEntity
import com.example.lifeos.data.repository.ReminderRepository
import com.example.lifeos.service.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _reminders =
        MutableStateFlow<List<ReminderEntity>>(emptyList())

    val reminders: StateFlow<List<ReminderEntity>> =
        _reminders.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllReminders().collect { reminderList ->
                _reminders.value = reminderList
            }
        }
    }

    fun addReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val generatedId = repository.addReminder(reminder)

            if (reminder.isNotificationEnabled && !reminder.isCompleted) {
                reminderScheduler.scheduleReminder(
                    reminderId = generatedId.toInt(),
                    title = reminder.title,
                    description = reminder.description,
                    reminderTime = reminder.reminderTime
                )
            }
        }
    }

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderScheduler.cancelReminder(reminder.id)

            repository.updateReminder(reminder)

            if (reminder.isNotificationEnabled && !reminder.isCompleted) {
                reminderScheduler.scheduleReminder(
                    reminderId = reminder.id,
                    title = reminder.title,
                    description = reminder.description,
                    reminderTime = reminder.reminderTime
                )
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderScheduler.cancelReminder(reminder.id)
            repository.deleteReminder(reminder)
        }
    }
}
