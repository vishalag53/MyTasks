package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vishalag53.mytasks.Tasks.Repository.TasksListDetailsRepository

class TasksListsDetailsViewModel(
    private val tasksListDetailsRepository: TasksListDetailsRepository
) : ViewModel(){

    private val _newTitle = MutableLiveData<String>()
    val newTitle : LiveData<String>
        get() = _newTitle

    fun setNewTitle(newTitle: String) {
        _newTitle.value = newTitle
    }

    private val _newDetails = MutableLiveData<String>()
    val newDetails: LiveData<String>
        get() = _newDetails

    fun setNewDetails(newDetails: String){
        _newDetails.value = newDetails
    }

    val title : LiveData<List<String>> = tasksListDetailsRepository.title
    val details : LiveData<List<String>> = tasksListDetailsRepository.details
    val date : LiveData<List<String>> = tasksListDetailsRepository.date
    val time : LiveData<List<String>> = tasksListDetailsRepository.time
    val repeat : LiveData<List<String>> = tasksListDetailsRepository.repeat
    val important : LiveData<List<String>> = tasksListDetailsRepository.important
    val completed : LiveData<List<String>> = tasksListDetailsRepository.completed

}