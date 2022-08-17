package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsView(todoId: String, navigator: NavController, roomDb: AppDatabase) {
    val context = LocalContext.current
    var todo by remember {
        mutableStateOf<Todo?>(null)
    }

    CoroutineScope(Dispatchers.IO).launch {
        var result = roomDb.todoDao().selectById(todoId.toInt())
        withContext(Dispatchers.Main) {
            todo = result
        }
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
                Text(text = "Todo will be removed",
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
                                roomDb.todoDao().deleteById(todoId.toInt())

                                withContext(Dispatchers.Main) {
                                    navigator.popBackStack()
                                }
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

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(title = {
            Text(text = "Details")
        }, actions = {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = {
                    isDelConfirmOpen = true
                }, Modifier.padding(end = 15.dp)) {
                    Icon(Icons.Rounded.Delete,
                        contentDescription = "Delete all todos")
                }
                IconButton(onClick = {
                    navigator.popBackStack()
                }, Modifier.padding(end = 25.dp)) {
                    Icon(Icons.Rounded.ArrowBack,
                        contentDescription = "Delete todos with status 'is done'")
                }
            }
        })
        Text(text = "All Details",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            textAlign = TextAlign.Center,
            fontSize = 28.sp,)
        TextField(value = "${todo?.title ?: "Not set!"}",
            onValueChange = {
                todo?.title = it
                CoroutineScope(Dispatchers.IO).launch {
                    roomDb.todoDao().update(todo!!)
            }
        }, label = {
            Text(text = "Title")
        }, placeholder = {
            Text(text = "To-Do title")
        }, modifier = Modifier.padding(top = 15.dp, bottom = 10.dp))
        TextField(value = "${todo?.notes ?: "Not set!"}",
            onValueChange = {
                todo?.notes = it
                CoroutineScope(Dispatchers.IO).launch {
                    roomDb.todoDao().update(todo!!)
                }
            }, label = {
                Text(text = "Notes")
            }, placeholder = {
                Text(text = "Additional information")
            }, modifier = Modifier.padding(top = 15.dp, bottom = 10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "To-Do is done", fontWeight = FontWeight.Bold)
            Checkbox(
                checked = todo?.isDone ?: false,
                onCheckedChange = {
                    todo?.isDone = it
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDb.todoDao().update(todo!!)
                    }
                }
            )
        }
        Button(modifier = Modifier.padding(top = 25.dp),
            onClick = {
                if ((todo?.title?.length ?: 0) < 3) {
                    Toast.makeText(context,
                        "Please provide a title with at least 3 characters.",
                        Toast.LENGTH_LONG).show();
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDb.todoDao().update(todo!!)
                    }
                    Toast.makeText(context, "Updated!", Toast.LENGTH_LONG).show();
                }
        }) {
            Text("Update")
        }
    }
}