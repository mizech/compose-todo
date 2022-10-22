package com.mizech.compose_todo.ui.theme

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mizech.compose_todo.*
import com.mizech.compose_todo.R
import com.mizech.compose_todo.data.AppDatabase
import com.mizech.compose_todo.data.MainViewModel
import com.mizech.compose_todo.data.Todo
import com.mizech.compose_todo.data.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsView(todoId: String, todoText: String, todoNote: String,
                navigator: NavController, roomDb: AppDatabase,
                viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val toastMinChars = stringResource(R.string.toast_min_chars)
    val messageMaxChars = stringResource(R.string.toast_max_title)
    val toastNoteChars = stringResource(R.string.message_max_note)
    var todo by remember {
        mutableStateOf<Todo?>(null)
    }
    var sTitle by remember {
        mutableStateOf(todoText)
    }
    var sNotes by remember {
        mutableStateOf(todoNote)
    }
    var uriImage by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) {
        uriImage = it
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    fun selectTodoById() {
        CoroutineScope(Dispatchers.IO).launch {
            var result = roomDb.todoDao().selectById(todoId.toInt())
            withContext(Dispatchers.Main) {
                todo = result
                sTitle = "${result.title}"
                sNotes = "${result.notes}"
            }
        }
    }

    selectTodoById()

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

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
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
            value = "${sTitle ?: "Not set!"}",
            onValueChange = {
                if (it.length > Utils.maxTitleLength) {
                    viewModel.currentTitleError = true
                    return@TextField
                } else {
                    viewModel.currentTitleError = false
                }

                CoroutineScope(Dispatchers.IO).launch {
                    todo?.title = it
                    todo?.modifiedAt = System.currentTimeMillis()
                    roomDb.todoDao().update(todo!!)
                }
        }, label = {
            Text(text = stringResource(R.string.text_title))
        }, isError = viewModel.currentTitleError,
            placeholder = {
            Text(text = stringResource(R.string.placeholder_title))
        }, modifier = Modifier
                .padding(
                    top = 15.dp, bottom = 10.dp,
                    start = 25.dp, end = 25.dp
                )
                .fillMaxWidth())
        if (viewModel.currentTitleError) {
            Text(messageMaxChars,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))
        }
        TextField(value = "${sNotes ?: "Not set!"}",
            onValueChange = {
                if (it.length > Utils.maxNoteLength) {
                    viewModel.currentNoteError = true
                    return@TextField
                } else {
                    viewModel.currentNoteError = false
                }

                sNotes = it
                todo?.notes = it
                todo?.modifiedAt = System.currentTimeMillis()
                CoroutineScope(Dispatchers.IO).launch {
                    todo?.notes = it
                    todo?.modifiedAt = System.currentTimeMillis()
                    roomDb.todoDao().update(todo!!)
                    withContext(Dispatchers.Main) {
                        sNotes = it
                    }
                }
            }, label = {
                Text(text = stringResource(R.string.text_notes))
            }, isError = viewModel.currentNoteError,
            placeholder = {
                Text(text = stringResource(R.string.text_additional_info))
            }, modifier = Modifier
                .padding(
                    top = 15.dp, bottom = 10.dp,
                    start = 25.dp, end = 25.dp
                )
                .fillMaxWidth())
            if (viewModel.currentNoteError) {
                Text(
                    stringResource(R.string.message_max_note),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))
            }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.text_done),
                fontWeight = FontWeight.Bold)
            Checkbox(
                checked = todo?.isDone ?: false,
                onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        todo?.isDone = it
                        todo?.modifiedAt = System.currentTimeMillis()
                        roomDb.todoDao().update(todo!!)
                    }
                }
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.text_important),
                fontWeight = FontWeight.Bold)
            Checkbox(
                checked = todo?.isImportant ?: false,
                onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        todo?.isImportant = it
                        todo?.modifiedAt = System.currentTimeMillis()
                        roomDb.todoDao().update(todo!!)
                    }
                }
            )
        }
        /*
            todo: uriImage in der DB speichern
         */
        uriImage?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val dataImage = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(dataImage)
            }

            bitmap.value?.let {
                Image(bitmap = it.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 50.dp, end = 50.dp, bottom = 15.dp)
                )
            }
        }
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text("Select Image")
        }
        Text("${stringResource(R.string.label_created)} " +
                "${Utils.createDateTimeStr(todo?.createdAt ?: 0L)}",
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp))
        Text("${stringResource(R.string.label_modified)} " +
                "${Utils.createDateTimeStr(todo?.modifiedAt ?: 0L)}",
            modifier = Modifier.padding(bottom = 30.dp))
    }
}