
package com.example.lifeos.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeos.data.local.entity.HabitCompletionEntity
import com.example.lifeos.data.local.entity.HabitEntity
import com.example.lifeos.data.repository.HabitSchedule
import com.example.lifeos.data.repository.HabitTrackingEngine
import com.example.lifeos.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class HabitDayStats(
    val scheduled: Int,
    val completed: Int,
    val partial: Int
)

private data class HabitPeriodStats(
    val scheduled: Int,
    val completed: Int
)

@Composable
fun HabitHistorySection(
    habits: List<HabitEntity>,
    completions: Map<Int, List<HabitCompletionEntity>>,
    today: LocalDate = LocalDate.now()
) {
    var displayedMonth by remember {
        mutableStateOf(YearMonth.from(today))
    }

    var selectedDate by remember {
        mutableStateOf(today)
    }

    val monthFormatter = remember {
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    }

    val calendarDates = remember(displayedMonth) {
        val firstDay = displayedMonth.atDay(1)
        val offset = firstDay.dayOfWeek.value - 1

        buildList<LocalDate?> {
            repeat(offset) { add(null) }

            for (day in 1..displayedMonth.lengthOfMonth()) {
                add(displayedMonth.atDay(day))
            }

            while (size % 7 != 0) {
                add(null)
            }
        }
    }

    val selectedStats = remember(habits, completions, selectedDate) {
        getHabitDayStats(habits, completions, selectedDate)
    }

    val weeklyStats = remember(habits, completions, selectedDate, today) {
        val weekStart = HabitSchedule.getWeekStart(selectedDate)
        val weekEnd = minOf(weekStart.plusDays(6), today)

        getHabitPeriodStats(
            habits,
            completions,
            weekStart,
            weekEnd
        )
    }

    val monthlyStats = remember(habits, completions, displayedMonth, today) {
        val monthStart = displayedMonth.atDay(1)
        val monthEnd = minOf(displayedMonth.atEndOfMonth(), today)

        if (monthStart.isAfter(today)) {
            HabitPeriodStats(0, 0)
        } else {
            getHabitPeriodStats(
                habits,
                completions,
                monthStart,
                monthEnd
            )
        }
    }

    val longestStreak = remember(habits, completions, today) {
        habits.maxOfOrNull { habit ->
            calculateLongestStreak(
                habit,
                completions[habit.id].orEmpty(),
                today
            )
        } ?: 0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LifeOSSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Your Consistency",
                    color = LifeOSText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Every small win adds up.",
                    color = LifeOSMuted,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HistorySummaryCard(
                    modifier = Modifier.weight(1f),
                    label = "This week",
                    value = "${weeklyStats.completed}/${weeklyStats.scheduled}",
                    accent = LifeOSPurple
                )

                HistorySummaryCard(
                    modifier = Modifier.weight(1f),
                    label = "This month",
                    value = "${monthlyStats.completed}/${monthlyStats.scheduled}",
                    accent = LifeOSGreen
                )

                HistorySummaryCard(
                    modifier = Modifier.weight(1f),
                    label = "Longest streak",
                    value = "$longestStreak",
                    accent = LifeOSOrange
                )
            }

            HorizontalDivider(color = LifeOSOutline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity Calendar",
                    color = LifeOSText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        displayedMonth = displayedMonth.minusMonths(1)
                        selectedDate = displayedMonth.atEndOfMonth()
                    }
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month",
                        tint = LifeOSPurple
                    )
                }

                Text(
                    text = displayedMonth.format(monthFormatter),
                    color = LifeOSPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = {
                        val nextMonth = displayedMonth.plusMonths(1)

                        if (!nextMonth.atDay(1).isAfter(today)) {
                            displayedMonth = nextMonth
                            selectedDate = minOf(
                                nextMonth.atEndOfMonth(),
                                today
                            )
                        }
                    },
                    enabled = displayedMonth < YearMonth.from(today)
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month",
                        tint = if (displayedMonth < YearMonth.from(today)) {
                            LifeOSPurple
                        } else {
                            LifeOSMuted
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        color = LifeOSMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            calendarDates.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { date ->
                        if (date == null) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            )
                        } else {
                            val stats = remember(
                                habits,
                                completions,
                                date
                            ) {
                                getHabitDayStats(habits, completions, date)
                            }

                            val dayColor = getHeatmapColor(
                                date,
                                stats,
                                today
                            )

                            val isSelected = selectedDate == date

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(dayColor)
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(
                                                width = 2.dp,
                                                color = LifeOSPurple,
                                                shape = CircleShape
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable {
                                        selectedDate = date
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = when {
                                        date.isAfter(today) -> LifeOSMuted
                                        stats.scheduled == 0 -> LifeOSMuted
                                        stats.completed == stats.scheduled ->
                                            Color.White
                                        else -> LifeOSText
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Activity legend",
                    color = LifeOSText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem(LifeOSGreen, "Completed")
                    LegendItem(LifeOSOrange, "Partial")
                    LegendItem(Color(0xFFFFE5E8), "Missed")
                    LegendItem(Color(0xFFF1F0F7), "No schedule")
                }
            }

            HorizontalDivider(color = LifeOSOutline)

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = selectedDate.format(
                        DateTimeFormatter.ofPattern(
                            "EEEE, dd MMMM yyyy",
                            Locale.getDefault()
                        )
                    ),
                    color = LifeOSText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                if (selectedStats.scheduled == 0) {
                    Text(
                        text = "No habits scheduled for this day.",
                        color = LifeOSMuted,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = "${selectedStats.completed} of ${selectedStats.scheduled} habits completed",
                        color = LifeOSPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    habits.filter {
                        HabitSchedule.isScheduledForDate(it, selectedDate)
                    }.forEach { habit ->
                        val completion = completions[habit.id]
                            .orEmpty()
                            .firstOrNull {
                                it.completionDate == selectedDate.toString()
                            }

                        val successful = completion != null &&
                                HabitTrackingEngine.isCompletionSuccessful(
                                    habit,
                                    completion
                                )

                        val partial = completion != null && !successful

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .background(
                                        color = when {
                                            successful -> LifeOSGreen
                                            partial -> LifeOSOrange
                                            else -> Color(0xFFE7A0AA)
                                        },
                                        shape = CircleShape
                                    )
                            )

                            Spacer(Modifier.width(9.dp))

                            Text(
                                text = habit.name,
                                modifier = Modifier.weight(1f),
                                color = LifeOSText,
                                fontSize = 12.sp
                            )

                            Text(
                                text = when {
                                    successful -> "Completed"
                                    partial -> "Partial"
                                    selectedDate.isAfter(today) -> "Upcoming"
                                    selectedDate == today -> "Pending"
                                    else -> "Missed"
                                },
                                color = when {
                                    successful -> LifeOSGreen
                                    partial -> LifeOSOrange
                                    else -> LifeOSMuted
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySummaryCard(
    modifier: Modifier,
    label: String,
    value: String,
    accent: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LifeOSBackground)
            .padding(10.dp)
    ) {
        Text(
            text = value,
            color = accent,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(3.dp))

        Text(
            text = label,
            color = LifeOSMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = label,
            color = LifeOSMuted,
            fontSize = 9.sp
        )
    }
}

private fun getHabitDayStats(
    habits: List<HabitEntity>,
    completions: Map<Int, List<HabitCompletionEntity>>,
    date: LocalDate
): HabitDayStats {
    val scheduledHabits = habits.filter {
        HabitSchedule.isScheduledForDate(it, date)
    }

    var completed = 0
    var partial = 0

    scheduledHabits.forEach { habit ->
        val completion = completions[habit.id]
            .orEmpty()
            .firstOrNull {
                it.completionDate == date.toString()
            }

        if (completion != null) {
            if (
                HabitTrackingEngine.isCompletionSuccessful(
                    habit,
                    completion
                )
            ) {
                completed++
            } else if (habit.targetValue != null) {
                partial++
            }
        }
    }

    return HabitDayStats(
        scheduled = scheduledHabits.size,
        completed = completed,
        partial = partial
    )
}

private fun getHeatmapColor(
    date: LocalDate,
    stats: HabitDayStats,
    today: LocalDate
): Color {
    if (date.isAfter(today) || stats.scheduled == 0) {
        return Color(0xFFF1F0F7)
    }

    return when {
        stats.completed == stats.scheduled -> LifeOSGreen
        stats.completed > 0 -> LifeOSPurpleSoft
        stats.partial > 0 -> Color(0xFFFFE8C9)
        date == today -> Color(0xFFF1F0F7)
        else -> Color(0xFFFFE5E8)
    }
}

private fun getHabitPeriodStats(
    habits: List<HabitEntity>,
    completions: Map<Int, List<HabitCompletionEntity>>,
    startDate: LocalDate,
    endDate: LocalDate
): HabitPeriodStats {
    if (endDate.isBefore(startDate)) {
        return HabitPeriodStats(0, 0)
    }

    var scheduledCount = 0
    var completedCount = 0

    var date = startDate

    while (!date.isAfter(endDate)) {
        val stats = getHabitDayStats(habits, completions, date)

        scheduledCount += stats.scheduled
        completedCount += stats.completed

        date = date.plusDays(1)
    }

    return HabitPeriodStats(
        scheduled = scheduledCount,
        completed = completedCount
    )
}

private fun calculateLongestStreak(
    habit: HabitEntity,
    completions: List<HabitCompletionEntity>,
    today: LocalDate
): Int {
    val successfulDates = HabitTrackingEngine.getSuccessfulDates(
        habit,
        completions
    )

    if (successfulDates.isEmpty()) return 0

    val startDate = java.time.Instant.ofEpochMilli(habit.startDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    if (habit.frequency == "WEEKLY_TARGET") {
        val required = habit.targetPerWeek.coerceIn(1, 7)

        var weekStart = HabitSchedule.getWeekStart(startDate)
        val currentWeekStart = HabitSchedule.getWeekStart(today)

        var currentRun = 0
        var longestRun = 0

        while (!weekStart.isAfter(currentWeekStart)) {
            val weekEnd = weekStart.plusDays(6)

            val completedCount = successfulDates.count { date ->
                !date.isBefore(weekStart) && !date.isAfter(weekEnd)
            }

            val isCurrentWeek = weekStart == currentWeekStart

            if (completedCount >= required) {
                currentRun++
                longestRun = maxOf(longestRun, currentRun)
            } else if (!isCurrentWeek) {
                currentRun = 0
            }

            weekStart = weekStart.plusWeeks(1)
        }

        return longestRun
    }

    var date = startDate
    var currentRun = 0
    var longestRun = 0

    while (!date.isAfter(today)) {
        if (HabitSchedule.isScheduledForDate(habit, date)) {
            if (date in successfulDates) {
                currentRun++
                longestRun = maxOf(longestRun, currentRun)
            } else if (date.isBefore(today)) {
                currentRun = 0
            }
        }

        date = date.plusDays(1)
    }

    return longestRun
}
