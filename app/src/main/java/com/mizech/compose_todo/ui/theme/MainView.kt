package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.RoomDatabase
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.Todo
import com.mizech.compose_todo.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalMaterialApi
@SuppressLint("UnrememberedMutableState")
@Composable
fun MainView(navigator: NavController, roomDb: AppDatabase) {
    var todos = remember {
        mutableStateListOf<Todo>()
    }

    var currentText by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = currentText, onValueChange = {
            currentText = it
        }, label = {
            Text(text = "Add new To-Do")
        }, placeholder = {
            Text(text = "What has to be done?")
        }, modifier = Modifier.padding(top = 20.dp, bottom = 10.dp))
        Button(modifier = Modifier.padding(bottom = 10.dp), onClick = {
            var textToDisplay = currentText
            if (textToDisplay.length > 45) {
                textToDisplay = "${currentText.substring(0, 40)} ... "
            }
            val todo = Todo()
            todo.text = currentText

            runBlocking {
                launch {
                    roomDb.todoDao().insertAll(todo)
                }
            }
            todos.add(todo)
            currentText = ""
        }) {
            Text("Insert new To-Do")
        }

        LazyColumn(horizontalAlignment = Alignment.Start) {
            items(todos.count()) { index ->
                Card(onClick = {
                    navigator.navigate("details")
                }, elevation = 5.dp) {
                    Text(text = todos.get(index).text,
                        modifier = Modifier.padding(all = 10.dp))
                }
            }
        }
    }
}