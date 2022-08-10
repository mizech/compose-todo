package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var todo by remember {
        mutableStateOf<Todo?>(null)
    }

    CoroutineScope(Dispatchers.IO).launch {
        var result = roomDb.todoDao().selectById(todoId.toInt())
        withContext(Dispatchers.Main) {
            todo = result
        }
    }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar( elevation = 4.dp, backgroundColor = Color.LightGray) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    navigator.popBackStack()
                }) {
                    Text(" <- ")
                }
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDb.todoDao().deleteById(todoId.toInt())

                        withContext(Dispatchers.Main) {
                            navigator.popBackStack()
                        }
                    }
                }) {
                    Text("Delete Todo")
                }
            }
        }
        Text(text = "To-Do Details",
            fontSize = 32.sp,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp))
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

        // Todo: Von Text zu TextField aendern.
        Text(text = "Notes", fontWeight = FontWeight.Bold)
        Text(text = "${todo?.notes ?: "Not set!"}",
            modifier = Modifier.padding(bottom = 20.dp))
        // --------------------------------------------------

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
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().update(todo!!)
            }
        }) {
            Text("Update")
        }
    }
}