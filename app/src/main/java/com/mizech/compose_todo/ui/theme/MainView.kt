package com.mizech.compose_todo.ui.theme

import Todo
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@SuppressLint("UnrememberedMutableState")
@Composable
fun MainView() {
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

            todos.add(Todo(text = textToDisplay))
            currentText = ""
        }) {
            Text("Insert new To-Do")
        }
        LazyColumn(horizontalAlignment = Alignment.Start) {
            items(todos.count()) { index ->
                Card(onClick = { }, elevation = 5.dp) {
                    Text(text = todos.get(index).text,
                        modifier = Modifier.padding(all = 10.dp))
                }
            }
        }
    }
}