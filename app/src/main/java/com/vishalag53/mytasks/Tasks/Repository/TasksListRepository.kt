package com.vishalag53.mytasks.Tasks.Repository

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.data.TasksList

class TasksListRepository(
    private val requireContext: Context,
    private val databaseReference: DatabaseReference,
    databaseReferencePrevious: DatabaseReference
) {

    val data: LiveData<List<TasksList>> = MutableLiveData()
    private var mutableTaskList: MutableList<TasksList> = mutableListOf()

    val dataName : LiveData<List<String>> = MutableLiveData()
    private val _dataName : MutableList<String> = mutableListOf()

    val dataCompleteTasks: LiveData<List<TasksList>> = MutableLiveData()
    private var mutableCompleteTaskList: MutableList<TasksList> = mutableListOf()

    private val  valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            mutableTaskList.clear()
            mutableCompleteTaskList.clear()
            for(taskSnapshot in snapshot.children){
                val key = taskSnapshot.key!!
                val title: String = taskSnapshot.child("Title").value.toString().trim()
                val detail: String = taskSnapshot.child("Details").value.toString().trim()
                val date: String = taskSnapshot.child("Date").value.toString().trim()
                val time: String = taskSnapshot.child("Time").value.toString().trim()
                val repeat: String = taskSnapshot.child("Repeat").value.toString().trim()
                val important: String = taskSnapshot.child("Important").value.toString().trim()
                if(taskSnapshot.child("Completed").value.toString() == "true"){
                    val tasksList = TasksList(key,title,detail,date,time,repeat,important,"true")
                    mutableCompleteTaskList.add(tasksList)
                }
                else {
                    val tasksList = TasksList(key,title,detail,date,time,repeat,important,"false")
                    mutableTaskList.add(tasksList)
                }
            }
            (data as MutableLiveData<List<TasksList>>).postValue(mutableTaskList)
            (dataCompleteTasks as MutableLiveData<List<TasksList>>).postValue(mutableCompleteTaskList)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(requireContext,error.message,Toast.LENGTH_SHORT).show()
        }

    }

    private val  valueEventListenerData = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            _dataName.clear()
            for (taskSnapshot in snapshot.children){
                if(taskSnapshot.key == "Tasks Name"){
                    val name = taskSnapshot.value.toString()
                    _dataName.add(name)
                    break
                }
            }
            (dataName as MutableLiveData<List<String>>).postValue(_dataName)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(requireContext,error.message,Toast.LENGTH_SHORT).show()
        }

    }

    init {
        databaseReference.addValueEventListener(valueEventListener)
        databaseReferencePrevious.addValueEventListener(valueEventListenerData)
    }

    fun renameImportant(tasksList: TasksList){
        val important = if(tasksList.important == "true"){ "false" }  else{ "true" }
        databaseReference.child(tasksList.id).child("Important").setValue(important)
    }

    fun deleteTask(itemToDelete: TasksList){
        databaseReference.child(itemToDelete.id).removeValue()
    }

    fun addInFirebase(removedItem: TasksList){
        val key = databaseReference.push()
        key.child("Title").setValue(removedItem.title)
        key.child("Details").setValue(removedItem.details)
        key.child("Date").setValue(removedItem.date)
        key.child("Time").setValue(removedItem.time)
        key.child("Repeat").setValue(removedItem.repeat)
        key.child("Important").setValue(removedItem.important)
        key.child("Completed").setValue(removedItem.isCompleted)
    }

    fun isAddInCompleteTasksList(tasksList: TasksList, flag: String){
        val key = databaseReference.child(tasksList.id)
        key.child("Completed").setValue(flag)
    }

    fun deleteAllTasks(){
        databaseReference.removeValue()
    }

    fun renameList(databaseReferencePrevious: DatabaseReference) {
        val dialog = dialog()
        val addTitle = dialog.findViewById<EditText>(R.id.addTitle)

        addTitle.text = Editable.Factory.getInstance().newEditable(dataName.value?.get(0))
        addTitle.hint = "Rename Task List"

        val save = dialog.findViewById<TextView>(R.id.save)
        save.setTextColor(ContextCompat.getColor(requireContext,R.color.bkg_blue))
        save.setOnClickListener {
            val renameListName: String = addTitle.text.toString()
            if(renameListName.isNotEmpty()){
                databaseReferencePrevious.child("Tasks Name").setValue(renameListName).addOnCompleteListener {
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
        dialogExtracted(dialog)
    }

    private fun dialog(): Dialog {
        val dialog = Dialog(requireContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.create_dialog_box)

        dialog.findViewById<EditText>(R.id.addDetails).visibility = View.GONE
        dialog.findViewById<Button>(R.id.showDetailEditText).visibility = View.GONE
        dialog.findViewById<Button>(R.id.showCalendar).visibility = View.GONE
        dialog.findViewById<Button>(R.id.showTime).visibility = View.GONE
        dialog.findViewById<Button>(R.id.showRepeat).visibility = View.GONE
        dialog.findViewById<Button>(R.id.addImportant).visibility = View.GONE
        dialog.findViewById<ConstraintLayout>(R.id.showDateTimeRepeatDetail).visibility = View.GONE
        dialog.findViewById<ConstraintLayout>(R.id.clRemind).visibility = View.GONE

        return dialog
    }

    private fun dialogExtracted(dialog: Dialog) {
        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    fun deleteCompletedList() {
        databaseReference.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val querySnapshot = task.result
                for(snapShot in querySnapshot.children){
                    val key = snapShot.key!!
                    if(snapShot.child("Completed").value.toString() == "true"){
                        databaseReference.child(key).removeValue()
                    }
                }
            }
        }
    }

}