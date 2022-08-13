package com.mizech.compose_todo

import androidx.room.*

@Dao
interface TodoDao {
    @Insert
    suspend fun insertAll(vararg todos: Todo)

    @Query("SELECT * FROM todos ORDER BY isDone ASC")
    suspend fun selectAllTodos(): List<Todo>

    @Query("SELECT * FROM todos WHERE todos.todoId = :todoId")
    suspend fun selectById(todoId: Int): Todo

    @Query("DELETE FROM todos")
    suspend fun deleteAll()

    @Update
    suspend fun update(todo: Todo)

    @Query("DELETE FROM todos WHERE todos.todoId = :todoId")
    suspend fun deleteById(todoId: Int)
}