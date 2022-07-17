package com.mizech.compose_todo

import Todo
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mizech.compose_todo.ui.theme.ComposetodoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposetodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainView()
                }
            }
        }
    }
}

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
        Button(onClick = {
            todos.add(Todo(text = currentText))
            currentText = ""
        }) {
            Text("Insert new To-Do")
        }
        LazyColumn {
            items(todos.count()) { index ->
                Text(text = todos.get(index).text,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposetodoTheme {
        MainView()
    }
}