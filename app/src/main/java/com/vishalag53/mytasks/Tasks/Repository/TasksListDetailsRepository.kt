package com.vishalag53.mytasks.Tasks.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class TasksListDetailsRepository (
    private val requireContext: Context,
    private val databaseReferencePrevious: DatabaseReference,
    private val databaseReference: DatabaseReference
) {

    val title : LiveData<List<String>> = MutableLiveData()
    private val _title : MutableList<String> = mutableListOf()

    val details : LiveData<List<String>> = MutableLiveData()
    private val _details : MutableList<String> = mutableListOf()

    val date : LiveData<List<String>> = MutableLiveData()
    private val _date : MutableList<String> = mutableListOf()

    val time: LiveData<List<String>> = MutableLiveData()
    private val _time : MutableList<String> = mutableListOf()

    val repeat : LiveData<List<String>> = MutableLiveData()
    private val _repeat : MutableList<String> = mutableListOf()

    val important : LiveData<List<String>> = MutableLiveData()
    private val _important : MutableList<String> = mutableListOf()

    val completed : LiveData<List<String>> = MutableLiveData()
    private val _completed : MutableList<String> = mutableListOf()

    private val  valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            _title.clear()
            _details.clear()
            _date.clear()
            _time.clear()
            _repeat.clear()
            _important.clear()
            _completed.clear()
            for (taskSnapshot in snapshot.children){
                if(taskSnapshot.key == "Title"){
                    val title = taskSnapshot.value.toString().trim()
                    _title.add(title)
                }
                if(taskSnapshot.key == "Details"){
                    val details = taskSnapshot.value.toString().trim()
                    _details.add(details)
                }
                if(taskSnapshot.key == "Date"){
                    val date = taskSnapshot.value.toString().trim()
                    _date.add(date)
                }
                if(taskSnapshot.key == "Time"){
                    val time = taskSnapshot.value.toString().trim()
                    _time.add(time)
                }
                if(taskSnapshot.key == "Repeat"){
                    val repeat = taskSnapshot.value.toString().trim()
                    _repeat.add(repeat)
                }
                if(taskSnapshot.key == "Important"){
                    val important = taskSnapshot.value.toString().trim()
                    _important.add(important)
                }
                if(taskSnapshot.key == "Completed"){
                    val completed = taskSnapshot.value.toString().trim()
                    _completed.add(completed)
                }
            }
            (title as MutableLiveData<List<String>>).postValue(_title)
            (details as MutableLiveData<List<String>>).postValue(_details)
            (date as MutableLiveData<List<String>>).postValue(_date)
            (time as MutableLiveData<List<String>>).postValue(_time)
            (repeat as MutableLiveData<List<String>>).postValue(_repeat)
            (important as MutableLiveData<List<String>>).postValue(_important)
            (completed as MutableLiveData<List<String>>).postValue(_completed)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(requireContext,error.message, Toast.LENGTH_SHORT).show()
        }

    }

    init {
        databaseReference.addValueEventListener(valueEventListener)
    }
}