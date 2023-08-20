package com.vishalag53.mytasks.Tasks.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vishalag53.mytasks.Tasks.data.TasksList

class TasksListRepository(
    private val requireContext: Context,
    private val databaseReference: DatabaseReference
    ) {

    val data: LiveData<List<TasksList>> = MutableLiveData()
    private var mutableTaskList: MutableList<TasksList> = mutableListOf()

    private val  valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            mutableTaskList.clear()
            for(taskSnapshot in snapshot.children){
                val key = taskSnapshot.key!!
                val list = taskSnapshot.value.toString()
                val list1 = list.substring(8,list.length - 2)
                val listSplit = list1.split(",")
                var title: String
                var detail: String
                var date: String
                var time: String
                var repeat: String
                var important: String

                if(listSplit.size == 8){
                    title = listSplit[0].trim()
                    detail = listSplit[1].trim()
                    date = listSplit[2].trim() + ", " + listSplit[3].trim() + ", " + listSplit[4].trim()
                    time = listSplit[5].trim()
                    repeat = listSplit[6].trim()
                    important = listSplit[7].trim()
                }
                else{
                    title = listSplit[0].trim()
                    detail = listSplit[1].trim()
                    date = listSplit[2]
                    time = listSplit[3].trim()
                    repeat = listSplit[4].trim()
                    important = listSplit[5].trim()
                }

                val tasksList = TasksList(key,title.trim(),detail.trim(),date.trim(),time.trim(),repeat.trim(),important.trim())

                mutableTaskList.add(tasksList)
            }

            (data as MutableLiveData<List<TasksList>>).postValue(mutableTaskList)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(requireContext,error.message,Toast.LENGTH_SHORT).show()
        }

    }

    init {
        databaseReference.addValueEventListener(valueEventListener)
    }

    fun renameImportant(tasksList: TasksList){
        val important = if(tasksList.important == "true"){ "false" }
                                else{ "true" }
        val tasksList1 : MutableList<String> = mutableListOf()
        tasksList1.add(0,tasksList.title)
        tasksList1.add(1,tasksList.details!!)
        tasksList1.add(2,tasksList.date!!)
        tasksList1.add(3,tasksList.time!!)
        tasksList1.add(4,tasksList.repeat!! )
        tasksList1.add(5,important)
        databaseReference.child(tasksList.id).child("lists").removeValue()
        databaseReference.child(tasksList.id).child("lists").setValue(tasksList1)
    }

    fun deleteTask(itemToDelete: TasksList){
        databaseReference.child(itemToDelete.id).removeValue()
    }

    fun addInFirebase(removedItem: TasksList){
        val tasksList : MutableList<String> = mutableListOf()
        tasksList.add(0,removedItem.title)
        tasksList.add(1,removedItem.details!!)
        tasksList.add(2,removedItem.date!!)
        tasksList.add(3,removedItem.time!!)
        tasksList.add(4,removedItem.repeat!! )
        tasksList.add(5,removedItem.important)
        databaseReference.push().child("lists").setValue(tasksList)
    }
}