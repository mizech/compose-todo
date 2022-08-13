package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.Todo
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

    var currentTitle by remember {
        mutableStateOf("")
    }

    var isDelConfirmOpen by remember {
        mutableStateOf(false)
    }

    if (isDelConfirmOpen) {
        AlertDialog(
            onDismissRequest = {
                isDelConfirmOpen = false
            },
            title = {
                Text(text = "All Todos will be removed",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            },
            text = {
                Column() {
                    Text("Do you want to continue?",
                        fontSize = 18.sp)
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                roomDb.todoDao().deleteAll()
                                todos.clear()
                            }
                            isDelConfirmOpen = false
                        }
                    ) {
                        Text("Continue")
                    }
                    Button(
                        onClick = {
                            isDelConfirmOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    Column(horizontalAlignment = Alignment.Start) {
        // Todo: Confirm-Modal "Loeschen-Bestaetigen"
        TopAppBar(elevation = 4.dp, backgroundColor = Color.LightGray) {
            Button(onClick = {
                isDelConfirmOpen = true
            }, modifier = Modifier.padding(start = 25.dp)) {
                Text("Delete all Todos")
            }
        }
        Column(modifier = Modifier.fillMaxSize()
            .padding(start = 25.dp, end = 25.dp)) {
            TextField(value = currentTitle, onValueChange = {
                currentTitle = it
            }, label = {
                Text(text = "Add new To-Do")
            }, placeholder = {
                Text(text = "What has to be done?")
            }, modifier = Modifier.padding(top = 15.dp, bottom = 10.dp))
            Button(modifier = Modifier.padding(bottom = 10.dp), onClick = {
                val todo = Todo()
                todo.title = currentTitle

                CoroutineScope(Dispatchers.IO).launch {
                    roomDb.todoDao().insertAll(todo)
                }

                currentTitle = ""
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
                            text = todos.get(index).title,
                            modifier = Modifier.padding(all = 10.dp)
                        )
                    }
                }
            }
        }
    }
}