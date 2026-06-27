package com.lifetracker.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifetracker.data.AppDatabase
import com.lifetracker.data.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TodoViewModel(private val db: AppDatabase) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _editingTodo = MutableStateFlow<Todo?>(null)
    val editingTodo: StateFlow<Todo?> = _editingTodo.asStateFlow()

    private val _titleInput = MutableStateFlow("")
    val titleInput: StateFlow<String> = _titleInput.asStateFlow()

    private val _contentInput = MutableStateFlow("")
    val contentInput: StateFlow<String> = _contentInput.asStateFlow()

    init {
        viewModelScope.launch {
            db.todoDao().getActive().collect { list ->
                _todos.value = list
            }
        }
    }

    fun onTitleChange(text: String) { _titleInput.value = text }
    fun onContentChange(text: String) { _contentInput.value = text }

    fun openAddDialog() {
        _editingTodo.value = null
        _titleInput.value = ""
        _contentInput.value = ""
        _showDialog.value = true
    }

    fun openEditDialog(todo: Todo) {
        _editingTodo.value = todo
        _titleInput.value = todo.title
        _contentInput.value = todo.content
        _showDialog.value = true
    }

    fun dismissDialog() { _showDialog.value = false }

    fun saveTodo() {
        val title = _titleInput.value.trim()
        if (title.isEmpty()) return
        viewModelScope.launch {
            val todo = _editingTodo.value
            if (todo == null) {
                db.todoDao().insert(Todo(title = title, content = _contentInput.value))
            } else {
                db.todoDao().update(todo.copy(title = title, content = _contentInput.value))
            }
            dismissDialog()
        }
    }

    fun toggleDone(todo: Todo) {
        viewModelScope.launch {
            db.todoDao().update(todo.copy(isDone = !todo.isDone))
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            db.todoDao().delete(todo)
        }
    }
}
