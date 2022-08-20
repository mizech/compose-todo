package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.ConfirmAlertDialog
import com.mizech.compose_todo.R
import com.mizech.compose_todo.Todo
import kotlinx.coroutines.*

@ExperimentalMaterialApi
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun MainView(navigator: NavController, roomDb: AppDatabase) {
    suspend fun selectAllTodos(todos: SnapshotStateList<Todo>) {
        var existing = roomDb.todoDao().selectAllTodos()
        withContext(Dispatchers.Main) {
            todos.clear()
            todos.addAll(existing)
        }
    }

    val context = LocalContext.current
    var todos = remember {
        mutableStateListOf<Todo>()
    }

    CoroutineScope(Dispatchers.IO).launch {
        selectAllTodos(todos)
    }

    var currentTitle by remember {
        mutableStateOf("")
    }

    var isDelAllConfirmOpen = remember {
        mutableStateOf(false)
    }

    var isDelDoneConfirmOpen = remember {
        mutableStateOf(false)
    }

    if (isDelAllConfirmOpen.value) {
        ConfirmAlertDialog(isDelConfirmOpen = isDelAllConfirmOpen,
                            messageText = stringResource(R.string.conf_del_all_title)) {
            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().deleteAll()
                todos.clear()
            }
        }
    }

    if (isDelDoneConfirmOpen.value) {
        ConfirmAlertDialog(isDelConfirmOpen = isDelDoneConfirmOpen,
            messageText = stringResource(R.string.conf_del_done_title)) {
            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().deleteByDone()
                selectAllTodos(todos)
            }
        }
    }

    Column(horizontalAlignment = Alignment.Start) {
        TopAppBar(title = {
                          Text(text = stringResource(id = R.string.bar_title_details))
        }, actions = {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = {
                    isDelDoneConfirmOpen.value = true
                }, Modifier.padding(end = 25.dp)) {
                    Icon(Icons.Rounded.Delete,
                        contentDescription = "Delete todos with status 'is done'")
                }
                IconButton(onClick = {
                    isDelAllConfirmOpen.value = true
                }, Modifier.padding(end = 15.dp)) {
                    Icon(Icons.Rounded.Warning,
                        contentDescription = "Delete all todos")
                }
            }
        })
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, end = 25.dp)) {
            TextField(value = currentTitle, onValueChange = {
                currentTitle = it
            }, label = {
                Text(text = "Add new To-Do")
            }, placeholder = {
                Text(text = "What has to be done?")
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 10.dp))
            Button(modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(), onClick = {
                if (currentTitle.length < 3) {
                    Toast.makeText(context,
                        "Please provide a title with at least 3 characters.",
                        Toast.LENGTH_LONG).show();
                } else {
                    val todo = Todo()
                    todo.title = currentTitle

                    CoroutineScope(Dispatchers.IO).launch {
                        roomDb.todoDao().insertAll(todo)
                    }

                    currentTitle = ""
                }
            }) {
                /*
                    todo: Disketten-Icon hinter dem Text.
                */
                Text(stringResource(R.string.button_insert))
            }

            /*
                todo: Zusaetzliche Attribute 'createdAt' und 'modifiedAt'
             */
            LazyColumn(horizontalAlignment = Alignment.Start) {
                items(todos.count()) { index ->
                    Card(onClick = {
                        navigator.navigate("details/${todos.get(index).id}")
                    }, elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
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