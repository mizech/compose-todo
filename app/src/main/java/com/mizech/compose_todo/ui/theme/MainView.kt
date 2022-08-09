package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.RoomDatabase
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.Todo
import com.mizech.compose_todo.TodoDao
import kotlinx.coroutines.*

@ExperimentalMaterialApi
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun MainView(navigator: NavController, roomDb: AppDatabase) {
    var todos = remember {
        mutableStateListOf<Todo>()
    }

    CoroutineScope(Dispatchers.IO).launch {
        var existing = roomDb.todoDao().selectAllTodos()
        withContext(Dispatchers.Main) {
            todos.clear()
            todos.addAll(existing)
        }
    }

    var currentText by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(elevation = 4.dp, backgroundColor = Color.LightGray) {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    roomDb.todoDao().deleteAll()
                    todos.clear()
                }
            }) {
                Text("Delete all Todos")
            }
        }
        TextField(value = currentText, onValueChange = {
            currentText = it
        }, label = {
            Text(text = "Add new To-Do")
        }, placeholder = {
            Text(text = "What has to be done?")
        }, modifier = Modifier.padding(top = 15.dp, bottom = 10.dp))
        Button(modifier = Modifier.padding(bottom = 10.dp), onClick = {
            val todo = Todo()
            todo.text = currentText

            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().insertAll(todo)
            }

            currentText = ""
        }) {
            Text("Insert new To-Do")
        }

        LazyColumn(horizontalAlignment = Alignment.Start) {
            items(todos.count()) { index ->
                Card(onClick = {
                    navigator.navigate("details/${todos.get(index).id}")
                }, elevation = 5.dp,
                    border = BorderStroke(3.dp,
                        SolidColor(if (todos[index].isDone)  Color.Green else Color.Red))) {
                    Text(
                        text = todos.get(index).text,
                        modifier = Modifier.padding(all = 10.dp)
                    )
                }
            }
        }
    }
}