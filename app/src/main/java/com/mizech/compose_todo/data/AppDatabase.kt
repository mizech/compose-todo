package com.mizech.compose_todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mizech.compose_todo.data.Todo
import com.mizech.compose_todo.data.TodoDao

@Database(entities = [(Todo::class)], version = 4)
public abstract class AppDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}