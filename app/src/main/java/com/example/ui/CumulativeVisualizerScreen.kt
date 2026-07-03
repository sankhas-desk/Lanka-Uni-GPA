package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile

@Composable
fun CumulativeVisualizerScreen(
    userProfile: UserProfile?,
    gpaStats: GpaStats,
    modifier: Modifier = Modifier
) {
    val completedSemesters = gpaStats.semesterWiseStats.count { it.sgpa > 0.0 }
    val maxGpa = if (userProfile?.gradingScale == "Moratuwa") 4.2 else 4.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper stats progress card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cumulative Standing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                CircularGpaGauge(
                    gpa = gpaStats.cumulativeGpa,
                    maxGpa = maxGpa,
                    classification = gpaStats.honorsClassification
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${gpaStats.totalCredits.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Credits Earned",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$completedSemesters / 8",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Semesters Active",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Grades Trend line Chart
        if (completedSemesters > 0) {
            Text(
                text = "GPA Trend Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start).padding(top = 8.dp, bottom = 2.dp)
            )

            GpaTrendChart(
                semesterStats = gpaStats.semesterWiseStats,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Sri Lankan Class Divisions Standards Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GoldenYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Degree Classification Criteria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), modifier = Modifier.padding(bottom = 12.dp))

                ClassificationRow(
                    title = "First Class Honours",
                    range = "GPA ≥ 3.70",
                    description = "Excellent academic standard. Widely required for local academy & research postings.",
                    isSelected = gpaStats.cumulativeGpa >= 3.70,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                ClassificationRow(
                    title = "Second Class Upper Division",
                    range = "3.30 – 3.69",
                    description = "Highly prestigious. Highly valued in public/private sectors and top tech roles.",
                    isSelected = gpaStats.cumulativeGpa in 3.30..3.69,
                    tint = EmeraldAccent
                )

                Spacer(modifier = Modifier.height(10.dp))

                ClassificationRow(
                    title = "Second Class Lower Division",
                    range = "3.00 – 3.29",
                    description = "Solid standing. Offers opportunities in competitive postgraduate paths.",
                    isSelected = gpaStats.cumulativeGpa in 3.00..3.29,
                    tint = GoldenYellow
                )

                Spacer(modifier = Modifier.height(10.dp))

                ClassificationRow(
                    title = "Ordinary Pass",
                    range = "2.00 – 2.99",
                    description = "General pass degree requirement standard.",
                    isSelected = gpaStats.cumulativeGpa in 2.00..2.99,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // Academic advice and prediction card
        if (completedSemesters > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Target Forecast & Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val adviceText = when {
                            gpaStats.cumulativeGpa >= 3.70 -> {
                                "Congratulations! You are currently on track for a First Class degree. Maintain your focus on critical research papers and major GPA modules to secure this elite standing."
                            }
                            gpaStats.cumulativeGpa >= 3.30 -> {
                                val delta = 3.70 - gpaStats.cumulativeGpa
                                "You are just ${String.format("%.2f", delta)} GPA points away from a First Class! Aim for A- and A grades in your upcoming semesters to hit the 3.70 threshold."
                            }
                            gpaStats.cumulativeGpa >= 3.00 -> {
                                val delta = 3.30 - gpaStats.cumulativeGpa
                                "You are on track for a Second Class Lower. With a small stretch of ${String.format("%.2f", delta)} grade points, you can secure a Second Class Upper Division standing."
                            }
                            gpaStats.cumulativeGpa >= 2.00 -> {
                                val delta = 3.00 - gpaStats.cumulativeGpa
                                "To elevate your qualifications to Second Class Lower, you need to hike your grades up by a cumulative average of ${String.format("%.2f", delta)} points. Select high-credit elective modules carefully!"
                            }
                            else -> {
                                "Academic Warning level. Please consult your university academic counselor and schedule extra revisions to raise your cumulative GPA above a 2.00 pass standard."
                            }
                        }

                        Text(
                            text = adviceText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        } else {
            // Emptystate advice card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Once you log courses and modules on the 'Calculator' tab, your dynamic trend lines and tailored class projections will populate right here.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ClassificationRow(
    title: String,
    range: String,
    description: String,
    isSelected: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) tint.copy(alpha = 0.12f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) tint else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = tint)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isSelected) tint else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = range,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = tint
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = 15.sp
            )
        }
    }
}
