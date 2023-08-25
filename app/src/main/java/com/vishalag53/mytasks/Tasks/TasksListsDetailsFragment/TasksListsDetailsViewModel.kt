package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vishalag53.mytasks.Tasks.Repository.TasksListDetailsRepository

class TasksListsDetailsViewModel(
    private val tasksListDetailsRepository: TasksListDetailsRepository
) : ViewModel(){

    // set title
    private val _newTitle = MutableLiveData<String>()
    val newTitle : LiveData<String>
        get() = _newTitle

    fun setNewTitle(newTitle: String) {
        _newTitle.value = newTitle
    }

    // set details
    private val _newDetails = MutableLiveData<String>()
    val newDetails: LiveData<String>
        get() = _newDetails

    fun setNewDetails(newDetails: String){
        _newDetails.value = newDetails
    }

    // get All
    val title : LiveData<List<String>> = tasksListDetailsRepository.title
    val details : LiveData<List<String>> = tasksListDetailsRepository.details
    val date : LiveData<List<String>> = tasksListDetailsRepository.date
    val time : LiveData<List<String>> = tasksListDetailsRepository.time
    val repeat : LiveData<List<String>> = tasksListDetailsRepository.repeat
    val important : LiveData<List<String>> = tasksListDetailsRepository.important
    val completed : LiveData<List<String>> = tasksListDetailsRepository.completed

    // set date
    private val _newDate = MutableLiveData<String>()
    val newDate : LiveData<String>
        get() = _newDate

    fun setNewDate(newDate: String){
        _newDate.value = newDate
    }

    // set time
    private val _newTime = MutableLiveData<String>()
    val newTime : LiveData<String>
        get() = _newTime

    fun setNewTime(newTime: String){
        _newTime.value = newTime
    }

    // set repeat

    private val _newRepeat = MutableLiveData<String>()
    val newRepeat : LiveData<String>
        get() = _newRepeat

    fun setNewRepeat(newRepeat:String){
        _newRepeat.value = newRepeat
    }
}