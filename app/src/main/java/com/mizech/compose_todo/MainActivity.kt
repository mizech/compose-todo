@file:OptIn(ExperimentalMaterialApi::class)

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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mizech.compose_todo.ui.theme.ComposetodoTheme
import com.mizech.compose_todo.ui.theme.DetailsView
import com.mizech.compose_todo.ui.theme.MainView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposetodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") { MainView(navigator = navController) }
                        composable("details") { DetailsView() }
                    }
                }
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposetodoTheme {
        MainView(navigator = NavController())
    }
}
 */