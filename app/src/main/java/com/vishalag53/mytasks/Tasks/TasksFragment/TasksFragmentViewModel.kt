package com.vishalag53.mytasks.Tasks.TasksFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vishalag53.mytasks.Tasks.Repository.TasksRepository
import com.vishalag53.mytasks.Tasks.data.NameList

class TasksFragmentViewModel(
    private val tasksRepository: TasksRepository,
) : ViewModel() {

    var data: LiveData<List<NameList>> = tasksRepository.data

    private val _sortType = MutableLiveData<String>()
    val sortType: LiveData<String>
        get() = _sortType

    fun getSortType(sortType: String){
        _sortType.value = sortType
    }

    fun getNameList(): LiveData<List<NameList>>{
        val cacheNameList = CacheManager.get("nameListCacheKey") as? List<NameList>
        return if(cacheNameList != null){
            MutableLiveData(cacheNameList)
        }
        else{
            val newData = tasksRepository. getNameList()
            MutableLiveData(newData)
        }
    }

}