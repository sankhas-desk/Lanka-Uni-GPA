package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Course
import com.example.data.VALID_GRADES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCalculatorScreen(
    semesterWiseStats: List<SemesterStats>,
    onAddCourse: (Int, String, String, Double, String) -> Unit,
    onUpdateCourse: (Course) -> Unit,
    onDeleteCourse: (Course) -> Unit,
    onClearSemester: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSemesterIndex by remember { mutableIntStateOf(0) } // Semester 1 represents index 0
    val activeSemesterNumber = selectedSemesterIndex + 1

    var showAddDialog by remember { mutableStateOf(false) }
    var courseToEdit by remember { mutableStateOf<Course?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    val activeStats = semesterWiseStats.find { it.semesterNumber == activeSemesterNumber }
        ?: SemesterStats(activeSemesterNumber, 0.0, 0.0, 0.0, emptyList())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(4.dp), // geometric
                modifier = Modifier
                    .testTag("add_course_fab")
                    .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal scrollable semester selector pills
            ScrollableTabRow(
                selectedTabIndex = selectedSemesterIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                for (i in 0..7) {
                    val label = when (i + 1) {
                        1 -> "Y1 S1"
                        2 -> "Y1 S2"
                        3 -> "Y2 S1"
                        4 -> "Y2 S2"
                        5 -> "Y3 S1"
                        6 -> "Y3 S2"
                        7 -> "Y4 S1"
                        8 -> "Y4 S2"
                        else -> "Sem ${i + 1}"
                    }
                    val isSelected = selectedSemesterIndex == i
                    Tab(
                        selected = isSelected,
                        onClick = { selectedSemesterIndex = i },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Semester GPA Info Cards & Details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        activeStats.sgpa >= 3.7 -> MaterialTheme.colorScheme.primaryContainer
                        activeStats.sgpa >= 3.0 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        activeStats.sgpa > 0.0 -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Semester ${activeSemesterNumber} Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total Credits: ${String.format("%.1f", activeStats.totalCredits)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }

                    // Large circular/pill representation of SGPA
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
                            .background(
                                color = when {
                                    activeStats.sgpa >= 3.7 -> EmeraldDeep
                                    activeStats.sgpa >= 3.0 -> GoldenYellow
                                    activeStats.sgpa > 0 -> CeylonOrange
                                    else -> Color.Gray
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.2f", activeStats.sgpa),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "SGPA",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Quick reset list operation bar
            if (activeStats.courses.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enrolled Courses",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }
            }

            // Courses List inside LazyColumn
            if (activeStats.courses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Subscribed Courses",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click the '+' button below to add your modules, credit weights, and grades.",
                            fontSize = 13.sp,
                            color = Color.Gray.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = activeStats.courses,
                        key = { it.id }
                    ) { course ->
                        CourseRowItem(
                            course = course,
                            onEditClick = { courseToEdit = course },
                            onDeleteClick = { onDeleteCourse(course) }
                        )
                    }
                }
            }
        }
    }

    // Add Course Dialog
    if (showAddDialog) {
        CourseFormDialog(
            title = "Add Course / Module",
            onDismiss = { showAddDialog = false },
            onSubmit = { code, name, creds, grade ->
                onAddCourse(activeSemesterNumber, code, name, creds, grade)
                showAddDialog = false
            }
        )
    }

    // Edit Course Dialog
    courseToEdit?.let { course ->
        CourseFormDialog(
            title = "Edit Course / Module",
            course = course,
            onDismiss = { courseToEdit = null },
            onSubmit = { code, name, creds, grade ->
                onUpdateCourse(course.copy(code = code, name = name, credits = creds, grade = grade))
                courseToEdit = null
            }
        )
    }

    // Clear Semester Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Reset Semester?") },
            text = { Text("Are you sure you want to delete all registered courses in Semester $activeSemesterNumber? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearSemester(activeSemesterNumber)
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFC63A1D))
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CourseRowItem(
    course: Course,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .clickable { onEditClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Colored grade badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .background(
                            color = when (course.grade.uppercase()) {
                                "A+", "A", "A-" -> EmeraldDeep
                                "B+", "B", "B-" -> EmeraldAccent
                                "C+", "C" -> GoldenYellow
                                "C-", "D+", "D" -> CeylonOrange
                                else -> Color(0xFFC63A1D)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = course.grade,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = course.code.uppercase(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${String.format("%.1f", course.credits)} Credits",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Quick detaching delete bucket
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove module",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormDialog(
    title: String,
    course: Course? = null,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Double, String) -> Unit
) {
    var code by remember { mutableStateOf(course?.code ?: "") }
    var name by remember { mutableStateOf(course?.name ?: "") }
    var creditsString by remember { mutableStateOf(course?.credits?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "3") }
    var selectedGrade by remember { mutableStateOf(course?.grade ?: "A") }

    var isGradeListExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(4.dp), // geometric
        modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp)),
        title = { Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Course Code Input
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course/Module Code (e.g. SENG 1122)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("course_code_input")
                )

                // Course Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Module Name (e.g. OOP)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("course_name_input")
                )

                // Credits Input slider or weights
                OutlinedTextField(
                    value = creditsString,
                    onValueChange = { creditsString = it },
                    label = { Text("Credits Weight (e.g. 2 or 3)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("course_credits_input")
                )

                // Standard grade pick dropdown
                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    OutlinedTextField(
                        value = "Grade: $selectedGrade",
                        onValueChange = {},
                        readOnly = true,
                        shape = RoundedCornerShape(4.dp),
                        trailingIcon = {
                            IconButton(onClick = { isGradeListExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand grade list")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isGradeListExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isGradeListExpanded,
                        onDismissRequest = { isGradeListExpanded = false }
                    ) {
                        VALID_GRADES.forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedGrade = g
                                    isGradeListExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val creditsValue = creditsString.toDoubleOrNull() ?: 3.0
                    if (code.isNotBlank() && name.isNotBlank()) {
                        onSubmit(code.trim().uppercase(), name.trim(), creditsValue, selectedGrade)
                    }
                },
                enabled = code.isNotBlank() && name.isNotBlank() && creditsString.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.testTag("course_dialog_confirm_button")
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
