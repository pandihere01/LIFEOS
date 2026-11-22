
package com.example.lifeos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeos.data.local.entity.CalendarEventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DaySchedule(
    events: List<CalendarEventEntity>,
    onEventClick: (CalendarEventEntity) -> Unit
) {
    if (events.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "No events scheduled for this day.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = events,
                key = { it.id }
            ) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun EventCard(
    event: CalendarEventEntity,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (event.isAllDay) {
                    "All day"
                } else {
                    "${timeFormat.format(Date(event.startTime))} - ${
                        timeFormat.format(Date(event.endTime))
                    }"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (event.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Location: ${event.location}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
