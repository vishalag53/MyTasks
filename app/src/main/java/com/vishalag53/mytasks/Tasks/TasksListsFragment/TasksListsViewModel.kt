package com.vishalag53.mytasks.Tasks.TasksListsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.Tasks.data.TasksList

class TasksListsViewModel(
    private val tasksListRepository: TasksListRepository
): ViewModel() {

    val data: LiveData<List<TasksList>> = tasksListRepository.data

    val dataCompletedTasks: LiveData<List<TasksList>> = tasksListRepository.dataCompleteTasks

    private val _sortType = MutableLiveData<String>()
    val sortType: LiveData<String>
        get() = _sortType

    fun getSortType(sortType: String){
        _sortType.value = sortType
    }


    val tasksName : LiveData<List<String>> = tasksListRepository.dataName

}