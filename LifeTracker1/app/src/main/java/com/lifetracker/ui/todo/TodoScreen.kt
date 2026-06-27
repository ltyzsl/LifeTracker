package com.lifetracker.ui.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifetracker.data.AppDatabase
import com.lifetracker.data.Todo
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TodoScreen() {
    val ctx = LocalContext.current.applicationContext as android.app.Application
    val vm: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(AppDatabase.getInstance(ctx))
    )
    val todos by vm.todos.collectAsState()
    val showDialog by vm.showDialog.collectAsState()
    val editingTodo by vm.editingTodo.collectAsState()
    val titleInput by vm.titleInput.collectAsState()
    val contentInput by vm.contentInput.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (todos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无待办", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos, key = { it.id }) { todo ->
                    TodoItem(
                        todo = todo,
                        onToggle = { vm.toggleDone(todo) },
                        onEdit = { vm.openEditDialog(todo) },
                        onDelete = { vm.deleteTodo(todo) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { vm.openAddDialog() },
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
        AlertDialog(
            onDismissRequest = { vm.dismissDialog() },
            confirmButton = {
                TextButton(onClick = { vm.saveTodo() }) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { vm.dismissDialog() }) { Text("取消") }
            },
            title = { Text(if (editingTodo == null) "新建待办" else "编辑待办") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { vm.onTitleChange(it) },
                        label = { Text("标题") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contentInput,
                        onValueChange = { vm.onContentChange(it) },
                        label = { Text("内容（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        )
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = todo.isDone, onCheckedChange = { onToggle() })

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                    color = if (todo.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                if (todo.content.isNotEmpty()) {
                    Text(
                        text = todo.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.Gray)
            }
        }
    }
}
