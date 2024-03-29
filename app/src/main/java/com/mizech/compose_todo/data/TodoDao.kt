package com.mizech.compose_todo.data

import androidx.room.*
import com.mizech.compose_todo.data.Todo

@Dao
interface TodoDao {
    @Insert
    suspend fun insertAll(vararg todos: Todo)

    @Query("SELECT * FROM todos ORDER BY isDone ASC, isImportant DESC, modifiedAt ASC")
    suspend fun selectAllTodos(): List<Todo>

    @Query("SELECT * FROM todos WHERE todos.todoId = :todoId")
    suspend fun selectById(todoId: Int): Todo?

    @Query("DELETE FROM todos")
    suspend fun deleteAll()

    @Query("DELETE FROM todos WHERE todos.isDone = 1")
    suspend fun deleteByDone()

    @Update
    suspend fun update(todo: Todo)

    @Query("DELETE FROM todos WHERE todos.todoId = :todoId")
    suspend fun deleteById(todoId: Int)
}