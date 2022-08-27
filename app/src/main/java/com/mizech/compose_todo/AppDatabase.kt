package com.mizech.compose_todo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(Todo::class)], version = 3)
public abstract class AppDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}