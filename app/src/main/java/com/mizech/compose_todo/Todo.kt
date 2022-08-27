package com.mizech.compose_todo

import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

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

    @ColumnInfo(name = "createdAt")
    var createdAt: Long = System.currentTimeMillis()

    @ColumnInfo(name = "modifiedAt")
    var modifiedAt: Long = System.currentTimeMillis()
}