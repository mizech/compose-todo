package com.mizech.compose_todo

import Todo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mizech.compose_todo.ui.theme.ComposetodoTheme

class MainActivity : ComponentActivity() {
    val todos = arrayListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todos.add(Todo("Alpha 01"))
        todos.add(Todo("Alpha 02"))
        todos.add(Todo("Alpha 03"))
        todos.add(Todo("Alpha 04"))
        todos.add(Todo("Alpha 05"))

        setContent {
            ComposetodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainView(todos = todos)
                }
            }
        }
    }
}

@Composable
fun MainView(todos: ArrayList<Todo>) {
    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(todos.count()) { index ->
                Text(todos.get(index).text)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposetodoTheme {
        MainView(todos = arrayListOf(Todo("", false)))
    }
}