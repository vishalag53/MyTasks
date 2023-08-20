package com.vishalag53.mytasks.Tasks.TasksListsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.Tasks.data.TasksList

class TasksListsViewModel(
    private val tasksListRepository: TasksListRepository
): ViewModel() {

    val data: LiveData<List<TasksList>> = tasksListRepository.data

}