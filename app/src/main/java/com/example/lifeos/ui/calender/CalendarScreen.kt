
package com.example.lifeos.ui.calender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeos.data.local.entity.CalendarEventEntity
import com.example.lifeos.ui.components.DaySchedule
import com.example.lifeos.ui.components.EventDialog
import com.example.lifeos.ui.components.MonthCalendar
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val CalendarBackground = Color(0xFF0B1020)
private val CalendarNavy = Color(0xFF171F33)
private val CalendarAccent = Color(0xFF36BFFA)
private val CalendarTextPrimary = Color.White
private val CalendarTextSecondary = Color(0xFFB0BECF)

@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    calendarViewModel: CalendarViewModel
) {
    val events by calendarViewModel.events.collectAsStateWithLifecycle()

    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var currentMonth by remember {
        mutableStateOf(YearMonth.now())
    }

    var showEventDialog by remember {
        mutableStateOf(false)
    }

    var editingEvent by remember {
        mutableStateOf<CalendarEventEntity?>(null)
    }

    val zone = ZoneId.systemDefault()

    // Events belonging to the selected date
    val selectedDayEvents = events.filter { event ->
        val eventStart = Instant.ofEpochMilli(event.startTime)
            .atZone(zone).toLocalDate()

        val eventEnd = Instant.ofEpochMilli(event.endTime)
            .atZone(zone).toLocalDate()

        !selectedDate.isBefore(eventStart) &&
                !selectedDate.isAfter(eventEnd) &&
                (
                        selectedDate != eventEnd ||
                                eventEnd != eventStart ||
                                event.endTime > selectedDate.atStartOfDay(zone)
                            .toInstant().toEpochMilli()
                        )
    }.sortedBy { it.startTime }

    // Dates containing events
    val eventDates = events.map { event ->
        Instant.ofEpochMilli(event.startTime)
            .atZone(zone).toLocalDate()
    }.toSet()

    Scaffold(
        containerColor = CalendarBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // HEADER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CalendarNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 18.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CalendarTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "LIFEOS CALENDAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = CalendarAccent
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = currentMonth.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy")
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = CalendarTextPrimary
                        )
                    }

                    TextButton(
                        onClick = {
                            selectedDate = LocalDate.now()
                            currentMonth = YearMonth.now()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = CalendarAccent
                        )
                    ) {
                        Text("Today")
                    }
                }
            }

            // MAIN CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CalendarBackground)
                    .padding(16.dp)
            ) {

                // CALENDAR CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CalendarNavy
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        MonthCalendar(
                            month = currentMonth,
                            selectedDate = selectedDate,
                            eventDates = eventDates,

                            onDateSelected = { date ->
                                selectedDate = date
                            },

                            onPreviousMonth = {
                                currentMonth = currentMonth.minusMonths(1)
                                selectedDate = currentMonth.atDay(1)
                            },

                            onNextMonth = {
                                currentMonth = currentMonth.plusMonths(1)
                                selectedDate = currentMonth.atDay(1)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SELECTED DATE + ADD BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedDate.format(
                                DateTimeFormatter.ofPattern("EEEE")
                            ).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CalendarAccent,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = selectedDate.format(
                                DateTimeFormatter.ofPattern("dd MMMM")
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CalendarTextPrimary
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            editingEvent = null
                            showEventDialog = true
                        },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = CalendarAccent,
                            contentColor = CalendarBackground
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Event"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // DAILY SCHEDULE
                if (selectedDayEvents.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No events",
                                style = MaterialTheme.typography.titleMedium,
                                color = CalendarTextPrimary,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Nothing scheduled for this day.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CalendarTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TextButton(
                                onClick = {
                                    editingEvent = null
                                    showEventDialog = true
                                }
                            ) {
                                Text(
                                    text = "+ Add your first event",
                                    color = CalendarAccent
                                )
                            }
                        }
                    }

                } else {
                    DaySchedule(
                        events = selectedDayEvents,
                        onEventClick = { event ->
                            editingEvent = event
                            showEventDialog = true
                        }
                    )
                }
            }
        }
    }

    // EVENT DIALOG
    if (showEventDialog) {
        EventDialog(
            selectedDate = selectedDate,
            existingEvent = editingEvent,

            onDismiss = {
                showEventDialog = false
                editingEvent = null
            },

            onSave = { event ->
                if (editingEvent == null) {
                    calendarViewModel.addEvent(event)
                } else {
                    calendarViewModel.updateEvent(event)
                }

                showEventDialog = false
                editingEvent = null
            },

            onDelete = { event ->
                calendarViewModel.deleteEvent(event)

                showEventDialog = false
                editingEvent = null
            }
        )
    }
}
