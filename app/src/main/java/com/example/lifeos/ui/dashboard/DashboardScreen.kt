package com.example.lifeos.ui.dashboard



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding

private val Background = Color(0xFF0B1020)
private val SurfaceDark = Color(0xFF171F33)
private val Accent = Color(0xFF36BFFA)

@Composable
fun DashboardScreen(onTasksClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            Text(
                text = "✨ LIFEOS",
                color = Accent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold

            )

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Hello Pandi!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "What would you like to do today?",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* Voice integration on Day 2 */ },
                    modifier = Modifier.size(170.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent
                    )
                ) {
                    Text(
                        text = "LIFEOS\nTAP TO TALK",
                        color = Background,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Text(
                text = "QUICK ACTIONS",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                //QuickAction("Tasks", Modifier.weight(1f))
                QuickAction(
                    "Tasks",
                    Modifier.weight(1f),
                    onClick = onTasksClick
                )
                QuickAction("Calendar", Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction("AI Chat", Modifier.weight(1f))
                QuickAction("My Goals", Modifier.weight(1f))
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "TODAY'S OVERVIEW",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Your personal dashboard is ready.",
                color = Color.LightGray,
                fontSize = 14.sp
            )
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
        modifier = modifier.height(85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
