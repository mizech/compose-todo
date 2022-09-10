package com.mizech.compose_todo.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    var currentTitle by mutableStateOf("")
    var currentTitleError by mutableStateOf(false)
    var currentNoteError by mutableStateOf(false)
}