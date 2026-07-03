package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AVATAR_TEMPLATES
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Sri Lankan theme color definitions
val EmeraldDeep = Color(0xFF0E5E4E)
val EmeraldLight = Color(0xFFF1FAF7)
val EmeraldAccent = Color(0xFF148E77)
val GoldenYellow = Color(0xFFE5A93B)
val CeylonOrange = Color(0xFFEF7C12)
val DarkCharcoal = Color(0xFF1A1F1E)

@Composable
fun SriLankanAvatar(
    dpIndicator: String,
    size: Int = 80,
    modifier: Modifier = Modifier
) {
    val isCustomUri = dpIndicator.startsWith("content://") || dpIndicator.startsWith("file://") || dpIndicator.startsWith("http")

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(EmeraldDeep, EmeraldAccent, GoldenYellow),
                    start = Offset(0f, 0f),
                    end = Offset(size.toFloat() * 2f, size.toFloat() * 2f)
                )
            )
            .border(2.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCustomUri) {
            AsyncImage(
                model = dpIndicator,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                error = null
            )
        } else {
            val emoji = when (dpIndicator) {
                "avatar_lion" -> "🦁"
                "avatar_peacock" -> "🦚"
                "avatar_lotus" -> "🌸"
                "avatar_grad" -> "🎓"
                "avatar_tea" -> "☕"
                "avatar_student" -> "🧑‍🎓"
                else -> "🦁"
            }
            Text(
                text = emoji,
                fontSize = (size * 0.45).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CircularGpaGauge(
    gpa: Double,
    maxGpa: Double = 4.0,
    modifier: Modifier = Modifier,
    classification: String = "Good Standing"
) {
    val animatedGpa = animateFloatAsState(
        targetValue = gpa.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "GpaGauge"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Background Arch (Track)
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Colored progress arc based on GPA
                val sweepAngle = (animatedGpa.value / maxGpa.toFloat()).coerceIn(0f, 1f) * 270f
                val arcColor = when {
                    gpa >= 3.7 -> EmeraldAccent
                    gpa >= 3.3 -> EmeraldDeep
                    gpa >= 3.0 -> GoldenYellow
                    gpa >= 2.0 -> CeylonOrange
                    else -> Color(0xFFC63A1D)
                }

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(arcColor, arcColor.copy(alpha = 0.7f), arcColor)
                    ),
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format("%.2f", gpa),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "CGPA / ${String.format("%.1f", maxGpa)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(
                    when {
                        gpa >= 3.7 -> 0xFFEBF6F4
                        gpa >= 3.3 -> 0xFFEEF6F4
                        gpa >= 3.0 -> 0xFFFEF9EB
                        else -> 0xFFFDF2F0
                    }
                )
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color(
                        when {
                            gpa >= 3.7 -> 0xFF0E5E4E
                            gpa >= 3.3 -> 0xFF148E77
                            gpa >= 3.0 -> 0xFFD2921D
                            else -> 0xFFC63A1D
                        }
                    ),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = classification,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(
                        when {
                            gpa >= 3.7 -> 0xFF0E5E4E
                            gpa >= 3.3 -> 0xFF148E77
                            gpa >= 3.0 -> 0xFF9E6E11
                            else -> 0xFFC63A1D
                        }
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun GpaTrendChart(
    semesterStats: List<SemesterStats>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val maxGpa = 4.2f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Calculate grid spacing
            val gridLines = 4 // 1.0, 2.0, 3.0, 4.0
            val lineYSpacing = height / (maxGpa + 0.3f)

            // Draw Y-Axis label guidelines (Grey dotted lines)
            for (i in 1..4) {
                val gpaVal = i.toFloat()
                val y = height - (gpaVal * (height / (maxGpa + 0.2f)))

                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(40.dp.toPx(), y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )

                // Label
                drawText(
                    textMeasurer = textMeasurer,
                    text = "$i.0",
                    style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                    topLeft = Offset(10.dp.toPx(), y - 8.dp.toPx())
                )
            }

            // Draw Semester columns and connect points with gradient lines
            val validSemesters = semesterStats.take(8)
            val stepX = (width - 50.dp.toPx()) / (validSemesters.size - 1).coerceAtLeast(1)

            val points = mutableListOf<Offset>()
            for (i in validSemesters.indices) {
                val gpa = validSemesters[i].sgpa.toFloat()
                val x = 40.dp.toPx() + (i * stepX)
                // Normalize y coordinate: height of 0 is bottom, height is top
                val y = height - (gpa * (height / (maxGpa + 0.2f)))
                points.add(Offset(x, y))
            }

            // Draw area gradient under the curve
            if (points.isNotEmpty()) {
                val path = Path().apply {
                    moveTo(points[0].x, height)
                    for (p in points) {
                        lineTo(p.x, p.y)
                    }
                    lineTo(points.last().x, height)
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(EmeraldAccent.copy(alpha = 0.35f), Color.Transparent)
                    )
                )

                // Draw connecting stroke
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = EmeraldDeep,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = null
                    )
                }

                // Draw points on top of curve
                for (i in points.indices) {
                    val sgpa = validSemesters[i].sgpa
                    val color = when {
                        sgpa >= 3.7 -> EmeraldAccent
                        sgpa >= 3.0 -> GoldenYellow
                        sgpa > 0.0 -> CeylonOrange
                        else -> Color.LightGray
                    }

                    // Border circles
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = points[i]
                    )

                    drawCircle(
                        color = color,
                        radius = 4.5.dp.toPx(),
                        center = points[i]
                    )

                    // Draw labels for semesters underneath the x-axis
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "S${i+1}",
                        style = TextStyle(fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold),
                        topLeft = Offset(points[i].x - 8.dp.toPx(), height - 12.dp.toPx())
                    )
                }
            }
        }
    }
}
