package com.vishalag53.mytasks.Tasks.Repository

import android.content.Context
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.TasksFragment.CacheManager
import com.vishalag53.mytasks.Tasks.Util.dialogTasksLists
import com.vishalag53.mytasks.Tasks.Util.dialogTasksListsBelow
import com.vishalag53.mytasks.Tasks.data.NameList

class TasksRepository(
    private val databaseReference: DatabaseReference,
    private val requireContext: Context
) {

    // fetch from the firebase

    val data: LiveData<List<NameList>> = MutableLiveData()
    private var mutableNameList: MutableList<NameList> = mutableListOf()

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            mutableNameList.clear()
            for (taskSnapshot   in snapshot.children) {
                val taskList = taskSnapshot.key?.let {
                    val name = taskSnapshot.child("Tasks Name").value.toString()
                    NameList(it, name)
                }

                if (taskList != null) {
                    mutableNameList.add(taskList)
                }
            }
            (data as MutableLiveData<List<NameList>>).postValue(mutableNameList)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(requireContext, error.message, Toast.LENGTH_SHORT).show()
        }
    }

    init {
        databaseReference.addValueEventListener(valueEventListener)
    }

    // create new task and add in firebase

     fun createTask() {
         val dialog = dialogTasksLists(requireContext)

         val addTitle = dialog.findViewById<EditText>(R.id.addTitle)
         addTitle.hint = "New Task List"

         val save = dialog.findViewById<TextView>(R.id.save)
         save.setTextColor(ContextCompat.getColor(requireContext,R.color.bkg_blue))
         save.setOnClickListener {
             val listName = addTitle.text.toString()
             if(listName.isNotEmpty()){
                 databaseReference.push().child("Tasks Name").setValue(listName).addOnCompleteListener {
                     if (it.isSuccessful){
                         Toast.makeText(requireContext,"Created  $listName",Toast.LENGTH_SHORT).show()
                         addTitle.text = null
                     }
                     else{
                         Log.d("VISHAL AGRAWAL","${it.exception?.message}")
                     }
                 }
             }
             else{
                 Toast.makeText(requireContext,"Enter the list name",Toast.LENGTH_SHORT).show()
             }
         }
         dialogTasksListsBelow(dialog)
     }

    // delete task from firebase

    fun deleteTask(itemToDelete: NameList) {
        databaseReference.child(itemToDelete.listNameId).removeValue()
    }

    // rename task in firebase

    fun renameTask(nameList: NameList) {
        val dialog = dialogTasksLists(requireContext)

        val addTitle = dialog.findViewById<EditText>(R.id.addTitle)
        addTitle.text = Editable.Factory.getInstance().newEditable(nameList.listNameName)
        addTitle.hint = "Rename Task List"

        val save = dialog.findViewById<TextView>(R.id.save)
        save.setTextColor(ContextCompat.getColor(requireContext,R.color.bkg_blue))
        save.setOnClickListener {
            val renameListName: String = addTitle.text.toString()
            if(renameListName.isNotEmpty()){
                databaseReference.child(nameList.listNameId).setValue(renameListName).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext, "Rename Successfully", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Log.d("VISHAL AGRAWAL","Rename ${it.exception.toString()}")
                    }
                }
            }
            else{
                Toast.makeText(requireContext,"Enter the list name",Toast.LENGTH_SHORT).show()
            }
        }
        dialogTasksListsBelow(dialog)
    }

    // item position changed in firebase

    fun itemPositionChangedInFirebase(fromPosition: Int, toPosition: Int) {
        val itemToPosition = data.value!![toPosition].listNameName
        val idToPosition = data.value!![toPosition].listNameId
        val itemFromPosition = data.value!![fromPosition].listNameName
        val idFromPosition = data.value!![fromPosition].listNameId

        databaseReference.child(idToPosition).setValue(itemFromPosition)
        databaseReference.child(idFromPosition).setValue(itemToPosition)
    }

    // Cache

    fun getNameList(): List<NameList>{
        val cacheNameList = CacheManager.get("nameListCacheKey") as? List<NameList>
        return if(cacheNameList != null){
            cacheNameList
        } else{
            val newData = fetchData()
            CacheManager.put("nameListCacheKey",newData)
            newData
        }
    }

    private fun fetchData(): List<NameList>{
        val mutableNameList1: MutableList<NameList> = mutableListOf()

        return if(mutableNameList.size > 15){
            var cnt = 0
            for (nameList in mutableNameList){
                mutableNameList1.add(nameList)
                cnt++
                if(cnt >= 15)   break
            }
            mutableNameList1
        } else{
            mutableNameList
        }
    }

    // again added in the firebase after the deleted

    fun addInFirebase(removedItem: String){
        databaseReference.push().child("Tasks Name").setValue(removedItem).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(requireContext,"Delete item again added",Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d("VISHAL AGRAWAL","Added Again ${it.exception.toString()}")
            }
        }
    }

}