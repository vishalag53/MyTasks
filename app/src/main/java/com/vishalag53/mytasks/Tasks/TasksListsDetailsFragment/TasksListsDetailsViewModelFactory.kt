package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishalag53.mytasks.Tasks.Repository.TasksListDetailsRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class TasksListsDetailsViewModelFactory(private val tasksListDetailsRepository: TasksListDetailsRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TasksListsDetailsViewModel::class.java)){
            return TasksListsDetailsViewModel(tasksListDetailsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}