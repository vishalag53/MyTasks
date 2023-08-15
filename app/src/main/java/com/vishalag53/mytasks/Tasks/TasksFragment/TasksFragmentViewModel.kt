package com.vishalag53.mytasks.Tasks.TasksFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vishalag53.mytasks.Tasks.Repository.TasksRepository
import com.vishalag53.mytasks.Tasks.data.NameList

class TasksFragmentViewModel(
    private val tasksRepository: TasksRepository,
) : ViewModel() {

    var data: LiveData<List<NameList>> = tasksRepository.data

    fun getCreate(){
        tasksRepository.createTask()
    }

    fun getDelete(itemToDelete: NameList) {
        tasksRepository.deleteTask(itemToDelete)
    }

    fun getRename(nameList: NameList) {
        tasksRepository.renameTask(nameList)
    }

}