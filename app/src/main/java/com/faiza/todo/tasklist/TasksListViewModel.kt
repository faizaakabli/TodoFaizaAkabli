package com.faiza.todo.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoFaizaAkabli.network.Api
import com.faiza.todo.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasksListViewModel : ViewModel() {
    private val webService = Api.userTaskWebService

    // privée mais modifiable à l'intérieur du VM:
    private val _tasksStateFlow = MutableStateFlow<List<Task>>(emptyList())
    // même donnée mais publique et non-modifiable à l'extérieur afin de pouvoir seulement s'y abonner:
    public val tasksStateFlow: StateFlow<List<Task>> = _tasksStateFlow.asStateFlow()

    suspend fun refresh() {
        viewModelScope.launch {
            val response = webService.getTasks() // Call HTTP (opération longue)
            if (response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
                val fetchedTasks = response.body()!!
                _tasksStateFlow.value = fetchedTasks // on modifie le flow, ce qui déclenche ses observers

            }

        }
    }

    suspend fun update(task: Task) {
        viewModelScope.launch {
            val response = webService.update(task, task.id) // TODO: appel réseau
            if (!response.isSuccessful) {
                val updatedTask = response.body()!!
                _tasksStateFlow.value = _tasksStateFlow.value - task + updatedTask
            }
        }

        }

        suspend fun create(task: Task) {
            viewModelScope.launch {
                val response = webService.create(task) // TODO: appel réseau
                if (!response.isSuccessful) {
                    val newTask = response.body()!!
                    _tasksStateFlow.value = _tasksStateFlow.value + newTask
                }
            }

            }


        suspend fun delete(task: Task) {
            viewModelScope.launch {
                val response = webService.delete(task.id) // TODO: appel réseau
                if (!response.isSuccessful) {
                    val newTask = response.body()!!
                    _tasksStateFlow.value = _tasksStateFlow.value - task
                }


            }
        }
    }
