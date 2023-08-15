package com.vishalag53.mytasks.Tasks.Repository

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vishalag53.mytasks.R
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
            for (taskSnapshot in snapshot.children) {
                val taskList = taskSnapshot.key?.let {
                    NameList(it, taskSnapshot.value.toString())
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
        val dialogBox = AlertDialog.Builder(requireContext)
        val editText = EditText(requireContext)
        dialogBox.setView(editText)
            .setIcon(R.drawable.baseline_add_task_24)
            .setTitle("Create New List")
            .setPositiveButton("CREATE LIST"){ _, _ ->
                val name = editText.text.toString()
                databaseReference.push().setValue(name).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext,"Created  $name",Toast.LENGTH_SHORT).show()
                        editText.text = null
                    }
                    else{
                        Log.d("VISHAL AGRAWAL","${it.exception?.message}")
                    }
                }
            }
            .setNegativeButton("Cancel",null)
            .show()
     }

    // delete task from firebase

    fun deleteTask(itemToDelete: NameList) {
        databaseReference.child(itemToDelete.listNameId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(requireContext, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d("VISHAL AGRAWAL","Delete ${it.exception.toString()}")
            }
        }
    }

    // rename task in firebase

    fun renameTask(nameList: NameList) {
        val dialogBox = AlertDialog.Builder(requireContext)
        val editText = EditText(requireContext)
        editText.setText(nameList.listNameName)
        dialogBox.setView(editText)
            .setIcon(R.drawable.edit_48px)
            .setTitle("Rename List")
            .setPositiveButton("Rename"){ _, _ ->
                val newName = editText.text.toString()
                databaseReference.child(nameList.listNameId).setValue(newName).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext, "Rename Successfully", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Log.d("VISHAL AGRAWAL","Rename ${it.exception.toString()}")
                    }
                }
            }
            .setNegativeButton("Cancel",null)
            .show()
    }
}