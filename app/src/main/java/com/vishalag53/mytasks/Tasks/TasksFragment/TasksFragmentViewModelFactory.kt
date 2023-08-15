package com.vishalag53.mytasks.Tasks.TasksFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishalag53.mytasks.Tasks.Repository.TasksRepository
import java.lang.IllegalArgumentException

class TasksFragmentViewModelFactory(
    private val tasksRepository: TasksRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TasksFragmentViewModel::class.java)){
            return TasksFragmentViewModel(tasksRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}