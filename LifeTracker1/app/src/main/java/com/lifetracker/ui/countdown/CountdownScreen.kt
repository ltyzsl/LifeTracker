package com.lifetracker.ui.countdown

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifetracker.data.AppDatabase
import com.lifetracker.data.Countdown
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CountdownScreen() {
    val ctx = LocalContext.current.applicationContext as android.app.Application
    val vm: CountdownViewModel = viewModel(
        factory = CountdownViewModelFactory(ctx, AppDatabase.getInstance(ctx))
    )
    val countdowns by vm.countdowns.collectAsState()
    val showDialog by vm.showDialog.collectAsState()
    val titleInput by vm.titleInput.collectAsState()
    val selectedDateTime by vm.selectedDateTime.collectAsState()

    // 统一计时器：每秒更新一次
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = LocalDateTime.now()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (countdowns.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无倒计时", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(countdowns, key = { it.id }) { countdown ->
                    CountdownItem(
                        countdown = countdown,
                        now = now,
                        onDelete = { vm.deleteCountdown(countdown) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { vm.showDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加")
        }
    }

    if (showDialog) {
        CountdownAddDialog(
            title = titleInput,
            onTitleChange = { vm.onTitleChange(it) },
            selectedDateTime = selectedDateTime,
            onDateTimeChange = { vm.onDateTimeChange(it) },
            onDismiss = { vm.dismissDialog() },
            onConfirm = { vm.addCountdown() }
        )
    }
}

@Composable
fun CountdownItem(countdown: Countdown, now: LocalDateTime, onDelete: () -> Unit) {
    val duration = remember(countdown.targetTime, now) {
        Duration.between(now, countdown.targetTime)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = countdown.title, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(4.dp))

                if (duration.isNegative) {
                    Text("已结束", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                } else {
                    val days = duration.toDays()
                    val hours = duration.toHours() % 24
                    val minutes = duration.toMinutes() % 60
                    val seconds = duration.seconds % 60
                    Text(
                        text = "${days}天 ${hours}时 ${minutes}分 ${seconds}秒",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "目标: ${countdown.targetTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownAddDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedDateTime: LocalDateTime,
    onDateTimeChange: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text("保存") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        title = { Text("新建倒计时") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(selectedDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(selectedDateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
                    }
                }
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateTimeChange(LocalDateTime.of(newDate, selectedDateTime.toLocalTime()))
                    }
                    showDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("取消") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedDateTime.hour,
            initialMinute = selectedDateTime.minute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateTimeChange(LocalDateTime.of(selectedDateTime.toLocalDate(), java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)))
                    showTimePicker = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("取消") } },
            text = { TimePicker(state = timePickerState) }
        )
    }
}
