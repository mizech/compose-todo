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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mizech.compose_todo.AppDatabase
import com.mizech.compose_todo.ConfirmAlertDialog
import com.mizech.compose_todo.R
import com.mizech.compose_todo.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsView(todoId: String, navigator: NavController, roomDb: AppDatabase) {
    val context = LocalContext.current
    val toastMinChars = stringResource(R.string.toast_min_chars)
    val toastUpdated = stringResource(R.string.toast_updated)
    var todo by remember {
        mutableStateOf<Todo?>(null)
    }

    CoroutineScope(Dispatchers.IO).launch {
        var result = roomDb.todoDao().selectById(todoId.toInt())
        withContext(Dispatchers.Main) {
            todo = result
        }
    }

    var isDelConfirmOpen = remember {
        mutableStateOf(false)
    }

    if (isDelConfirmOpen.value) {
        ConfirmAlertDialog(isDelConfirmOpen = isDelConfirmOpen,
            messageText = stringResource(R.string.confirm_del_todo)
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().deleteById(todoId.toInt())

                withContext(Dispatchers.Main) {
                    navigator.popBackStack()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(title = {
            Text(text = stringResource(id = R.string.bar_title_details))
        }, actions = {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = {
                    isDelConfirmOpen.value = true
                }, Modifier.padding(end = 15.dp)) {
                    Icon(Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.desc_del_todo))
                }
                IconButton(onClick = {
                    navigator.popBackStack()
                }, Modifier.padding(end = 25.dp)) {
                    Icon(Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.desc_back_nav))
                }
            }
        })
        TextField(
            value = "${todo?.title ?: "Not set!"}",
            onValueChange = {
                todo?.title = it
                CoroutineScope(Dispatchers.IO).launch {
                    roomDb.todoDao().update(todo!!)
                }
        }, label = {
            Text(text = stringResource(R.string.text_title))
        }, placeholder = {
            Text(text = stringResource(R.string.placeholder_title))
        }, modifier = Modifier
                .padding(
                    top = 15.dp, bottom = 10.dp,
                    start = 25.dp, end = 25.dp
                )
                .fillMaxWidth())
        TextField(value = "${todo?.notes ?: "Not set!"}",
            onValueChange = {
                todo?.notes = it
                CoroutineScope(Dispatchers.IO).launch {
                    roomDb.todoDao().update(todo!!)
                }
            }, label = {
                Text(text = stringResource(R.string.text_notes))
            }, placeholder = {
                Text(text = stringResource(R.string.text_additional_info))
            }, modifier = Modifier
                .padding(
                    top = 15.dp, bottom = 10.dp,
                    start = 25.dp, end = 25.dp
                )
                .fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.text_done),
                fontWeight = FontWeight.Bold)
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
    }
}