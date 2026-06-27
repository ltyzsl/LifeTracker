package com.lifetracker.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifetracker.data.AppDatabase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CheckInScreen() {
    val ctx = LocalContext.current.applicationContext as android.app.Application
    val vm: CheckInViewModel = viewModel(
        factory = CheckInViewModelFactory(AppDatabase.getInstance(ctx))
    )
    val habits by vm.habits.collectAsState()
    val selectedHabit by vm.selectedHabit.collectAsState()
    val checkIns by vm.checkIns.collectAsState()
    val currentMonth by vm.currentMonth.collectAsState()
    val showAddDialog by vm.showAddDialog.collectAsState()
    val newHabitName by vm.newHabitName.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 习惯标签栏
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(habits) { habit ->
                FilterChip(
                    selected = habit == selectedHabit,
                    onClick = { vm.selectHabit(habit) },
                    label = { Text(habit) }
                )
            }
            item {
                AssistChip(
                    onClick = { vm.showAddDialog() },
                    label = { Text("+ 新建") }
                )
            }
        }

        // 月份切换
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.previousMonth() }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "上月")
            }
            Text(
                text = "${currentMonth.year}年${currentMonth.monthValue}月",
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = { vm.nextMonth() }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "下月")
            }
        }

        // 日历网格
        CalendarGrid(
            yearMonth = currentMonth,
            checkInDates = checkIns.map { it.checkDate.toLocalDate() }.toSet(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 打卡按钮
        if (selectedHabit.isNotEmpty()) {
            Button(
                onClick = { vm.checkIn(selectedHabit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("今日打卡", modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        // 连续打卡天数
        val today = LocalDate.now()
        val streak = remember(checkIns) {
            var count = 0
            var date = today
            while (checkIns.any { it.checkDate.toLocalDate() == date }) {
                count++
                date = date.minusDays(1)
            }
            count
        }
        if (streak > 0) {
            Text(
                text = "连续打卡 $streak 天",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissAddDialog() },
            confirmButton = { TextButton(onClick = { vm.addHabit() }) { Text("保存") } },
            dismissButton = { TextButton(onClick = { vm.dismissAddDialog() }) { Text("取消") } },
            title = { Text("新建习惯") },
            text = {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { vm.onNewHabitNameChange(it) },
                    label = { Text("习惯名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    checkInDates: Set<LocalDate>,
    modifier: Modifier = Modifier
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = (firstDay.dayOfWeek.value % 7) // 让周日=0

    Column(modifier = modifier.fillMaxWidth()) {
        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 日期网格
        var dayCounter = 1
        val totalCells = ((startDayOfWeek + daysInMonth + 6) / 7) * 7

        for (week in 0 until totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val cellIndex = week * 7 + dayOfWeek
                    if (cellIndex < startDayOfWeek || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = yearMonth.atDay(dayCounter)
                        val checked = checkInDates.contains(date)
                        val isToday = date == LocalDate.now()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    color = when {
                                        checked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                color = when {
                                    checked -> Color.White
                                    isToday -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontSize = 13.sp
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
