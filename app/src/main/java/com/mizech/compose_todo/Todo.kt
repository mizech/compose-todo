package com.mizech.compose_todo

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
class Todo {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "todoId")
    var id: Int = 0

    @ColumnInfo(name = "text")
    var title: String = ""

    @ColumnInfo(name = "notes")
    var notes: String = ""

    @ColumnInfo(name = "isDone")
    var isDone: Boolean = false
}