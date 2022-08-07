package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsView(todoId: String, roomDb: AppDatabase) {
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
        Text(text = "To-Do Details",
            fontSize = 32.sp,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp))
        Text(text = "Title", fontWeight = FontWeight.Bold)
        Text(text = "${todo?.text ?: "Not set!"}",
            modifier = Modifier.padding(bottom = 20.dp))
        Text(text = "To-Do is done", fontWeight = FontWeight.Bold)
        Checkbox(
            checked = todo?.isDone ?: false,
            onCheckedChange = {
                todo?.isDone = !(todo?.isDone)!!
            }
        )
    }
}