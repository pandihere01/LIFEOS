
package com.example.lifeos.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeos.data.local.entity.GoalEntity
import com.example.lifeos.data.local.entity.GoalMilestoneEntity
import com.example.lifeos.ui.habits.HabitViewModel
import java.util.Locale

private val GoalPurple = Color(0xFF5938E8)
private val GoalBackground = Color(0xFFF7F7FF)
private val GoalSoftPurple = Color(0xFFEAE5FF)
private val GoalText = Color(0xFF20213A)
private val GoalMuted = Color(0xFF77778D)
private val GoalGreen = Color(0xFF20B486)
private val GoalBorder = Color(0xFFE5E3EF)

@Composable
fun GoalScreen(
    onBack: () -> Unit,
    goalViewModel: GoalViewModel,
    habitViewModel: HabitViewModel
) {
    val goals by goalViewModel.goals.collectAsState()
    val archivedGoals by goalViewModel.archivedGoals.collectAsState()

    var showArchivedGoals by remember {
        mutableStateOf(false)
    }
    val habits by habitViewModel.habits.collectAsState()
    val milestones by goalViewModel.milestones.collectAsState()
    val linkedHabitIds by goalViewModel.linkedHabitIds.collectAsState()

    var selectedGoal by remember { mutableStateOf<GoalEntity?>(null) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalEntity?>(null) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showMilestoneDialog by remember { mutableStateOf(false) }
    var showHabitDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(selectedGoal?.id) {
        selectedGoal?.let {
            goalViewModel.loadMilestones(it.id)
            goalViewModel.loadLinkedHabits(it.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GoalBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onBack() },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("‹", fontSize = 30.sp, color = GoalPurple)
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "LIFEOS",
                    fontSize = 12.sp,
                    color = GoalPurple,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "My Goals",
                    fontSize = 23.sp,
                    color = GoalText,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = CircleShape,
                color = GoalSoftPurple,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🎯", fontSize = 21.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GoalOverviewCard(goals = goals)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Your Goals",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoalText
                        )
                        Text(
                            "${goals.size} active goals",
                            fontSize = 12.sp,
                            color = GoalMuted
                        )
                    }

                    Button(
                        onClick = {
                            editingGoal = null
                            showGoalDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoalPurple
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("+ Add Goal")
                    }
                }
            }


            if (goals.isEmpty()) {
                item {
                    EmptyGoalsCard {
                        editingGoal = null
                        showGoalDialog = true
                    }
                }
            } else {
                goals.forEach { goal ->
                    item(key = "goal_${goal.id}") {
                        GoalCard(
                            goal = goal,
                            selected = selectedGoal?.id == goal.id,
                            onClick = {
                                selectedGoal = if (selectedGoal?.id == goal.id) {
                                    null
                                } else {
                                    goal
                                }
                            },
                            onProgress = {
                                selectedGoal = goal
                                showProgressDialog = true
                            },
                            onEdit = {
                                editingGoal = goal
                                showGoalDialog = true
                            },
                            onArchive = {
                                goalViewModel.archiveGoal(goal.id)
                                if (selectedGoal?.id == goal.id) {
                                    selectedGoal = null
                                }
                            },
                            onDelete = {
                                selectedGoal = goal
                                showDeleteConfirmation = true
                            }
                        )
                    }

                    if (selectedGoal?.id == goal.id) {
                        item(key = "details_${goal.id}") {
                            GoalDetailsSection(
                                goal = goal,
                                milestones = milestones,
                                linkedHabitIds = linkedHabitIds,
                                habits = habits,
                                onAddMilestone = {
                                    showMilestoneDialog = true
                                },
                                onCompleteMilestone = {
                                    goalViewModel.completeMilestone(it)
                                },
                                onDeleteMilestone = { milestone ->
                                    goalViewModel.deleteMilestone(milestone)
                                },
                                onManageHabits = {
                                    showHabitDialog = true
                                }
                            )
                        }
                    }
                }
            }


            // Archived goals section
            if (archivedGoals.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showArchivedGoals = !showArchivedGoals
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Archived Goals",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoalText
                            )

                            Text(
                                "${archivedGoals.size} archived goals",
                                fontSize = 12.sp,
                                color = GoalMuted
                            )
                        }

                        Text(
                            if (showArchivedGoals) "Hide ▲" else "View ▼",
                            color = GoalPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (showArchivedGoals) {
                    items(
                        items = archivedGoals,
                        key = { "archived_${it.id}" }
                    ) { archivedGoal ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                GoalBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoalSoftPurple,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            goalEmoji(archivedGoal.category),
                                            fontSize = 21.sp
                                        )
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        archivedGoal.title,
                                        color = GoalText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        "Archived",
                                        color = GoalMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        goalViewModel.restoreGoal(
                                            archivedGoal.id
                                        )
                                    }
                                ) {
                                    Text(
                                        "Restore",
                                        color = GoalPurple,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    if (showGoalDialog) {
        GoalEditorDialog(
            existingGoal = editingGoal,
            onDismiss = { showGoalDialog = false },
            onSave = { goal ->
                if (editingGoal == null) {
                    goalViewModel.addGoal(goal)
                } else {
                    goalViewModel.updateGoal(goal)
                }
                showGoalDialog = false
            }
        )
    }

    if (showProgressDialog && selectedGoal != null) {
        GoalProgressDialog(
            goal = selectedGoal!!,
            onDismiss = { showProgressDialog = false },
            onSave = { value ->
                goalViewModel.updateProgress(selectedGoal!!.id, value)
                showProgressDialog = false
            }
        )
    }

    if (showMilestoneDialog && selectedGoal != null) {
        MilestoneDialog(
            goalId = selectedGoal!!.id,
            onDismiss = { showMilestoneDialog = false },
            onSave = { milestone ->
                goalViewModel.addMilestone(milestone)
                showMilestoneDialog = false
            }
        )
    }

    if (showHabitDialog && selectedGoal != null) {
        HabitLinkDialog(
            habits = habits,
            linkedHabitIds = linkedHabitIds,
            onDismiss = { showHabitDialog = false },
            onToggle = { habitId, shouldLink ->
                if (shouldLink) {
                    goalViewModel.linkHabitToGoal(
                        habitId,
                        selectedGoal!!.id
                    )
                } else {
                    goalViewModel.unlinkHabitFromGoal(
                        habitId,
                        selectedGoal!!.id
                    )
                }
            }
        )
    }

    if (showDeleteConfirmation && selectedGoal != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete goal?") },
            text = {
                Text("This will permanently delete this goal and its milestones.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        goalViewModel.deleteGoal(selectedGoal!!)
                        selectedGoal = null
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFE45B68))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GoalOverviewCard(goals: List<GoalEntity>) {
    val completed = goals.count { it.isCompleted }
    val total = goals.size
    val progress = if (total == 0) 0f else completed.toFloat() / total

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GoalPurple),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "YOUR PROGRESS",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$completed",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    " / $total goals completed",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 7.dp)
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "${(progress * 100).toInt()}% completion",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: GoalEntity,
    selected: Boolean,
    onClick: () -> Unit,
    onProgress: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (goal.targetValue != null && goal.targetValue > 0) {
        (goal.currentValue / goal.targetValue).coerceIn(0.0, 1.0).toFloat()
    } else {
        if (goal.isCompleted) 1f else 0f
    }

    var showActions by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, GoalPurple)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, GoalBorder)
        },
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GoalSoftPurple,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            goalEmoji(goal.category),
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        goal.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GoalText
                    )
                    Text(
                        "${goal.category} • ${goal.goalType.replace("_", " ")}",
                        color = GoalMuted,
                        fontSize = 11.sp
                    )
                }

                Box {
                    TextButton(onClick = { showActions = true }) {
                        Text("⋮", fontSize = 22.sp, color = GoalMuted)
                    }

                    DropdownMenu(
                        expanded = showActions,
                        onDismissRequest = { showActions = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showActions = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Archive") },
                            onClick = {
                                showActions = false
                                onArchive()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showActions = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            if (goal.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    goal.description,
                    color = GoalMuted,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (goal.targetValue != null) {
                        "${formatGoalNumber(goal.currentValue)} / ${formatGoalNumber(goal.targetValue)} ${goal.unit}"
                    } else if (goal.isCompleted) {
                        "Completed"
                    } else {
                        "Not completed yet"
                    },
                    color = GoalText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    "${(progress * 100).toInt()}%",
                    color = GoalPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = if (goal.isCompleted) GoalGreen else GoalPurple,
                trackColor = GoalSoftPurple
            )

            if (goal.targetValue != null && !goal.isCompleted) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onProgress,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Progress", color = GoalPurple)
                }
            }

            if (goal.isCompleted) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "✓ Goal completed",
                    color = GoalGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            if (selected) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap to hide details",
                    color = GoalPurple,
                    fontSize = 11.sp
                )
            } else {
                Text(
                    "Tap to view milestones and linked habits",
                    color = GoalMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun GoalDetailsSection(
    goal: GoalEntity,
    milestones: List<GoalMilestoneEntity>,
    linkedHabitIds: List<Int>,
    habits: List<com.example.lifeos.data.local.entity.HabitEntity>,
    onAddMilestone: () -> Unit,
    onCompleteMilestone: (Int) -> Unit,
    onDeleteMilestone: (GoalMilestoneEntity) -> Unit,
    onManageHabits: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoalBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                "Goal Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GoalText
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Milestones",
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoalText
                )
                TextButton(onClick = onAddMilestone) {
                    Text("+ Add", color = GoalPurple)
                }
            }

            if (milestones.isEmpty()) {
                Text(
                    "No milestones yet. Break this goal into smaller steps.",
                    fontSize = 12.sp,
                    color = GoalMuted
                )
            } else {
                milestones.forEach { milestone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = milestone.isCompleted,
                            onCheckedChange = {
                                if (!milestone.isCompleted) {
                                    onCompleteMilestone(milestone.id)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GoalPurple
                            )
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                milestone.title,
                                color = if (milestone.isCompleted) {
                                    GoalMuted
                                } else {
                                    GoalText
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (milestone.description.isNotBlank()) {
                                Text(
                                    milestone.description,
                                    color = GoalMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        TextButton(
                            onClick = { onDeleteMilestone(milestone) }
                        ) {
                            Text("✕", color = GoalMuted)
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = GoalBorder
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Linked Habits",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoalText
                    )
                    Text(
                        "${linkedHabitIds.size} habits linked",
                        fontSize = 11.sp,
                        color = GoalMuted
                    )
                }

                TextButton(onClick = onManageHabits) {
                    Text("Manage", color = GoalPurple)
                }
            }

            val linkedHabits = habits.filter {
                it.id in linkedHabitIds
            }

            if (linkedHabits.isEmpty()) {
                Text(
                    "Link habits that help you achieve this goal.",
                    color = GoalMuted,
                    fontSize = 12.sp
                )
            } else {
                linkedHabits.forEach { habit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✓", color = GoalGreen)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            habit.name,
                            color = GoalText,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGoalsCard(onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎯", fontSize = 42.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "No goals yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GoalText
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Start turning your plans into achievements.",
                color = GoalMuted,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoalPurple
                )
            ) {
                Text("Create Your First Goal")
            }
        }
    }
}

@Composable
private fun GoalEditorDialog(
    existingGoal: GoalEntity?,
    onDismiss: () -> Unit,
    onSave: (GoalEntity) -> Unit
) {
    var title by remember(existingGoal) {
        mutableStateOf(existingGoal?.title.orEmpty())
    }
    var description by remember(existingGoal) {
        mutableStateOf(existingGoal?.description.orEmpty())
    }
    var category by remember(existingGoal) {
        mutableStateOf(existingGoal?.category ?: "Personal")
    }
    var goalType by remember(existingGoal) {
        mutableStateOf(existingGoal?.goalType ?: "SHORT_TERM")
    }
    var target by remember(existingGoal) {
        mutableStateOf(existingGoal?.targetValue?.toString().orEmpty())
    }
    var unit by remember(existingGoal) {
        mutableStateOf(existingGoal?.unit.orEmpty())
    }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingGoal == null) "Create Goal" else "Edit Goal")
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category", fontWeight = FontWeight.Medium)
                GoalChoiceRow(
                    options = listOf("Personal", "Career", "Health", "Finance"),
                    selected = category,
                    onSelect = { category = it }
                )

                Text("Goal duration", fontWeight = FontWeight.Medium)
                GoalChoiceRow(
                    options = listOf("SHORT_TERM", "LONG_TERM"),
                    selected = goalType,
                    onSelect = { goalType = it }
                )

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target value (optional)") },
                    placeholder = { Text("e.g. 100") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (e.g. books, km, problems)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Text(it, color = Color(0xFFE45B68), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedTarget = if (target.isBlank()) {
                        null
                    } else {
                        target.toDoubleOrNull()
                    }

                    when {
                        title.isBlank() -> error = "Enter a goal title."
                        target.isNotBlank() && (
                                parsedTarget == null ||
                                        !parsedTarget.isFinite() ||
                                        parsedTarget <= 0
                                ) -> error = "Enter a valid positive target."
                        else -> {
                            onSave(
                                GoalEntity(
                                    id = existingGoal?.id ?: 0,
                                    title = title.trim(),
                                    description = description.trim(),
                                    category = category,
                                    goalType = goalType,
                                    targetValue = parsedTarget,
                                    currentValue = existingGoal?.currentValue ?: 0.0,
                                    unit = unit.trim(),
                                    startDate = existingGoal?.startDate
                                        ?: System.currentTimeMillis(),
                                    deadline = existingGoal?.deadline,
                                    isCompleted = existingGoal?.isCompleted ?: false,
                                    isArchived = existingGoal?.isArchived ?: false,
                                    createdAt = existingGoal?.createdAt
                                        ?: System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            ) {
                Text("Save", color = GoalPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GoalProgressDialog(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var value by remember(goal.id) {
        mutableStateOf(goal.currentValue.toString())
    }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Progress") },
        text = {
            Column {
                Text(
                    "Target: ${formatGoalNumber(goal.targetValue ?: 0.0)} ${goal.unit}",
                    color = GoalMuted
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Current progress") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )
                error?.let {
                    Text(it, color = Color(0xFFE45B68))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val progress = value.toDoubleOrNull()
                    when {
                        progress == null || !progress.isFinite() || progress < 0 ->
                            error = "Enter a valid non-negative number."
                        else -> onSave(progress)
                    }
                }
            ) {
                Text("Save", color = GoalPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MilestoneDialog(
    goalId: Int,
    onDismiss: () -> Unit,
    onSave: (GoalMilestoneEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Milestone") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Milestone title") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            GoalMilestoneEntity(
                                goalId = goalId,
                                title = title.trim(),
                                description = description.trim()
                            )
                        )
                    }
                }
            ) {
                Text("Add", color = GoalPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun HabitLinkDialog(
    habits: List<com.example.lifeos.data.local.entity.HabitEntity>,
    linkedHabitIds: List<Int>,
    onDismiss: () -> Unit,
    onToggle: (Int, Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Link Habits") },
        text = {
            if (habits.isEmpty()) {
                Text(
                    "You haven't created any active habits yet.",
                    color = GoalMuted
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp)
                ) {
                    items(habits, key = { it.id }) { habit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onToggle(
                                        habit.id,
                                        habit.id !in linkedHabitIds
                                    )
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = habit.id in linkedHabitIds,
                                onCheckedChange = {
                                    onToggle(habit.id, it)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GoalPurple
                                )
                            )
                            Text(
                                habit.name,
                                color = GoalText,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = GoalPurple)
            }
        }
    )
}

@Composable
private fun GoalChoiceRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(option) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) GoalSoftPurple else GoalBackground,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) GoalPurple else GoalBorder
                )
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = 5.dp,
                        vertical = 10.dp
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option.replace("_", " "),
                        fontSize = 10.sp,
                        color = if (isSelected) GoalPurple else GoalMuted,
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}

private fun goalEmoji(category: String): String {
    return when (category.lowercase(Locale.ROOT)) {
        "career" -> "💼"
        "health" -> "💪"
        "finance" -> "💰"
        "personal" -> "🌱"
        "education" -> "📚"
        else -> "🎯"
    }
}

private fun formatGoalNumber(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
    }
}
