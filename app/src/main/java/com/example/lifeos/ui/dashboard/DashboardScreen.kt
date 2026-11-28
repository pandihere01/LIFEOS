
package com.example.lifeos.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.lifeos.ui.theme.*

@Composable
fun DashboardScreen(
    onTasksClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onRemindersClick: () -> Unit = {},
    onHabitsClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LifeOSBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "✨ LIFEOS",
                color = LifeOSPurple,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Hello Pandi! 👋",
                color = LifeOSText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "What would you like to do today?",
                color = LifeOSMuted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(28.dp))

            // Voice Assistant Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LifeOSPurple
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your AI Assistant",
                        color = LifeOSSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            // Voice integration will be added later.
                        },
                        modifier = Modifier.size(112.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LifeOSSurface,
                            contentColor = LifeOSPurple
                        )
                    ) {
                        Text(
                            text = "TAP\nTO TALK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Your personal AI companion",
                        color = LifeOSPurpleSoft,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // Quick Actions
            Text(
                text = "QUICK ACTIONS",
                color = LifeOSText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    "📋\nTasks",
                    Modifier.weight(1f),
                    onClick = onTasksClick
                )

                QuickAction(
                    "🎯\nMy Goals",
                    Modifier.weight(1f),
                    onClick = onGoalsClick
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    "⏰\nReminders",
                    Modifier.weight(1f),
                    onClick = onRemindersClick
                )

                QuickAction(
                    "🤖\nAI Chat",
                    Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    "📅\nCalendar",
                    Modifier.weight(1f),
                    onClick = onCalendarClick
                )

                QuickAction(
                    "🔥\nHabits",
                    Modifier.weight(1f),
                    onClick = onHabitsClick
                )
            }

            Spacer(Modifier.height(30.dp))

            // Overview
            Text(
                text = "TODAY'S OVERVIEW",
                color = LifeOSText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(15.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LifeOSSurface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Your personal dashboard is ready ✨",
                        color = LifeOSText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Stay focused, build better habits and make progress every day.",
                        color = LifeOSMuted,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickAction(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LifeOSSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = LifeOSText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }
    }
}
