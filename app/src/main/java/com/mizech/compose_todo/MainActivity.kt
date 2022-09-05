@file:OptIn(ExperimentalMaterialApi::class)

package com.mizech.compose_todo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.mizech.compose_todo.ui.theme.ComposetodoTheme
import com.mizech.compose_todo.ui.theme.DetailsView
import com.mizech.compose_todo.ui.theme.MainView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposetodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val roomDb = Room.databaseBuilder(
                        this.applicationContext,
                        AppDatabase::class.java,
                        "todos_database"
                    ).fallbackToDestructiveMigration().build()

                    NavHost(navController = navController,
                        startDestination = "main") {
                        composable("main") {
                            MainView(navigator = navController, roomDb = roomDb)
                        }
                        composable("details/{todoId}") {
                            val todoId = it.arguments?.get("todoId")!!
                            val todoText = it.arguments?.get("text")
                            var todoNote = it.arguments?.get("notes")

                            DetailsView(todoId = "${todoId}", todoText = "${todoText}",
                                todoNote = "${todoNote}", navigator = navController,
                                roomDb = roomDb)
                        }
                    }
                }
            }
        }
    }
}