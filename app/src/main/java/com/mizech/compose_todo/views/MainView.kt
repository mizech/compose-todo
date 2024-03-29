package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mizech.compose_todo.*
import com.mizech.compose_todo.R
import com.mizech.compose_todo.data.AppDatabase
import com.mizech.compose_todo.data.MainViewModel
import com.mizech.compose_todo.data.Todo
import com.mizech.compose_todo.data.Utils
import kotlinx.coroutines.*

@ExperimentalMaterialApi
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun MainView(
    navigator: NavController, roomDb: AppDatabase,
    viewModel: MainViewModel = viewModel()
) {
    suspend fun selectAllTodos(todos: MutableList<Todo>) {
        var existing = roomDb.todoDao().selectAllTodos()
        withContext(Dispatchers.Main) {
            todos.clear()
            todos.addAll(existing)
        }
    }

    val context = LocalContext.current
    val toastMaxChars = stringResource(R.string.toast_max_title)
    val toastMinChars = stringResource(R.string.toast_min_chars)
    var todos = remember {
        mutableStateListOf<Todo>()
    }

    CoroutineScope(Dispatchers.IO).launch {
        selectAllTodos(todos)
    }

    var isDelAllConfirmOpen = remember {
        mutableStateOf(false)
    }

    var isDelDoneConfirmOpen = remember {
        mutableStateOf(false)
    }

    if (isDelAllConfirmOpen.value) {
        ConfirmAlertDialog(
            isDelConfirmOpen = isDelAllConfirmOpen,
            messageText = stringResource(R.string.conf_del_all_title)
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().deleteAll()
                todos.clear()
            }
        }
    }

    if (isDelDoneConfirmOpen.value) {
        ConfirmAlertDialog(
            isDelConfirmOpen = isDelDoneConfirmOpen,
            messageText = stringResource(R.string.conf_del_done_title)
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                roomDb.todoDao().deleteByDone()
                selectAllTodos(todos)
            }
        }
    }

    Column(horizontalAlignment = Alignment.Start) {
        TopAppBar(title = {
            Text(text = stringResource(R.string.bar_title_all))
        }, actions = {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = {
                    isDelDoneConfirmOpen.value = true
                }, Modifier.padding(end = 25.dp)) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = stringResource(id = R.string.desc_del_all_done)
                    )
                }
                IconButton(onClick = {
                    isDelAllConfirmOpen.value = true
                }, Modifier.padding(end = 15.dp)) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = stringResource(id = R.string.desc_del_all)
                    )
                }
            }
        })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 25.dp)
        ) {
            TextField(value = viewModel.currentTitle, onValueChange = {
                if (it.length > Utils.maxTitleLength) {
                    viewModel.currentTitleError = true
                    return@TextField
                } else {
                    viewModel.currentTitleError = false
                }
                viewModel.currentTitle = it
            }, isError = viewModel.currentTitleError,
                label = {
                    Text(text = stringResource(id = R.string.text_add_todo))
                }, placeholder = {
                    Text(text = stringResource(id = R.string.placeholder_what_has))
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 10.dp)
            )
            if (viewModel.currentTitleError) {
                Text(
                    text = toastMaxChars,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            Button(modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(), onClick = {
                if (viewModel.currentTitle.length < 3) {
                    Toast.makeText(
                        context,
                        toastMinChars,
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    val todo = Todo()
                    todo.title = viewModel.currentTitle

                    CoroutineScope(Dispatchers.IO).launch {
                        roomDb.todoDao().insertAll(todo)

                        withContext(Dispatchers.Main) {
                            viewModel.currentTitle = ""
                        }
                    }
                }
            }) {
                Icon(Icons.Default.Add, "")
                Spacer(Modifier.size(ButtonDefaults.IconSize))
                Text(stringResource(R.string.button_insert))
            }

            LazyColumn(horizontalAlignment = Alignment.Start) {
                itemsIndexed(items = todos, key = { index, item -> item.hashCode() }) { index, item ->
                    val sCreated = Utils.createDateTimeStr(item.createdAt)
                    val sModified = Utils.createDateTimeStr(item.modifiedAt)
                    val sNotes = item.notes
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    item.isDone = !item.isDone
                                    item.modifiedAt = System.currentTimeMillis()
                                    roomDb.todoDao().update(item)
                                    selectAllTodos(todos)
                                }
                            }
                            true
                        }
                    )
                    SwipeToDismiss(state = dismissState,
                        background = {
                            val color = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Color.Transparent
                                DismissDirection.EndToStart -> Color.LightGray
                                null -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "",
                                    tint = Color.White,
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                            }
                        },
                        directions = setOf(DismissDirection.EndToStart),
                        dismissContent = {
                            Card(
                                onClick = {
                                    navigator.navigate("details/${item.id}")
                                }, elevation = 5.dp,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                border = BorderStroke(
                                    3.dp,
                                    SolidColor(
                                        if (item.isDone) Color.Green
                                        else if (item.isImportant) Color.Red else Color.Yellow
                                    )
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        top = 5.dp, bottom = 7.dp,
                                        start = 10.dp, end = 10.dp
                                    )
                                ) {
                                    if (todos.size > 0) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.h6,
                                            modifier = Modifier.padding()
                                        )
                                        if (sNotes.length > 0) {
                                            Text(
                                                text = if (sNotes.length > 100)
                                                    "${sNotes.subSequence(0, 94).toString()} ... "
                                                else item.notes,
                                                style = MaterialTheme.typography.body1,
                                                modifier = Modifier.padding()
                                            )
                                        }
                                        Text(
                                            text = "${stringResource(R.string.label_created)} ${sCreated}",
                                            modifier = Modifier.padding(
                                                start = 10.dp, end = 10.dp,
                                                top = 6.dp, bottom = 3.dp
                                            ),
                                            style = MaterialTheme.typography.caption
                                        )
                                        Text(
                                            text = "${stringResource(R.string.label_modified)} " +
                                                    "${sModified}",
                                            modifier = Modifier.padding(
                                                start = 10.dp, end = 10.dp,
                                                bottom = 5.dp
                                            ),
                                            style = MaterialTheme.typography.caption
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}