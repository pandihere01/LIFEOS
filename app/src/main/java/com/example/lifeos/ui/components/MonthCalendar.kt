
package com.example.lifeos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

// LIFEOS calendar colors
private val CalendarBackground = Color(0xFF0B1020)
private val CalendarSurface = Color(0xFF171F33)
private val CalendarAccent = Color(0xFF36BFFA)
private val CalendarText = Color(0xFFFFFFFF)
private val CalendarMuted = Color(0xFF9AA8BD)

// New event-date highlight color
private val CalendarEventDate = Color(0xFF263B5C)

@Composable
fun MonthCalendar(
    month: YearMonth,
    selectedDate: LocalDate,
    eventDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOffset = month.atDay(1).dayOfWeek.value % 7

    val dates = List(firstDayOffset) { null } +
            (1..daysInMonth).map { month.atDay(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CalendarSurface, MaterialTheme.shapes.large)
            .padding(12.dp)
    ) {

        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = CalendarAccent
                )
            }

            Text(
                text = "${month.month.name.lowercase()
                    .replaceFirstChar { it.uppercase() }} ${month.year}",
                style = MaterialTheme.typography.titleMedium,
                color = CalendarText
            )

            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = CalendarAccent
                )
            }
        }

        // Weekday headings
        val weekdays = listOf(
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = CalendarMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar dates
        dates.chunked(7).forEach { week ->

            Row(modifier = Modifier.fillMaxWidth()) {

                week.forEach { date ->

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        if (date != null) {

                            val isSelected = date == selectedDate
                            val hasEvent = date in eventDates

                            val dateBackground = when {
                                isSelected -> CalendarAccent
                                hasEvent -> CalendarEventDate
                                else -> CalendarBackground
                            }

                            val dateTextColor = when {
                                isSelected -> CalendarBackground
                                hasEvent -> CalendarAccent
                                else -> CalendarText
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        onDateSelected(date)
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                // Date circle
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(
                                            color = dateBackground,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = dateTextColor,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                // Event indicator
                                if (hasEvent) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(4.dp)
                                            .background(
                                                CalendarAccent,
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                repeat(7 - week.size) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}
