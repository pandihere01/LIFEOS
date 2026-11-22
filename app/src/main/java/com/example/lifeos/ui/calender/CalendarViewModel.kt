
package com.example.lifeos.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeos.data.local.entity.CalendarEventEntity
import com.example.lifeos.data.repository.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _events =
        MutableStateFlow<List<CalendarEventEntity>>(emptyList())

    val events: StateFlow<List<CalendarEventEntity>> =
        _events.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllEvents().collect { eventList ->
                _events.value = eventList
            }
        }
    }

    fun addEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.addEvent(event)
        }
    }

    fun updateEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.updateEvent(event)
        }
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }
}
