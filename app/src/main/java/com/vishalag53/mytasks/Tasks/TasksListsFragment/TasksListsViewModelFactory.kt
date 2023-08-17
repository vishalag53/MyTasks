package com.vishalag53.mytasks.Tasks.TasksListsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import java.lang.IllegalArgumentException

class TasksListsViewModelFactory( private val tasksListRepository: TasksListRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TasksListsViewModel::class.java)){
            return TasksListsViewModel(tasksListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}