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
import com.google.firebase.database.ktx.values
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.data.TasksList

class TasksListRepository(
    private val requireContext: Context,
    private val databaseReference: DatabaseReference
    ) {

    val data: LiveData<List<TasksList>> = MutableLiveData()
    private var mutableTaskList: MutableList<TasksList> = mutableListOf()

    val dataCompleteTasks: LiveData<List<TasksList>> = MutableLiveData()
    private var mutableCompleteTaskList: MutableList<TasksList> = mutableListOf()

    private val  valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            mutableTaskList.clear()
            mutableCompleteTaskList.clear()
            for(taskSnapshot in snapshot.children){
                val key = taskSnapshot.key!!
                if(key == "Completed Tasks"){
                    for(completedTasksSnapshot in snapshot.child("Completed Tasks").children){
                        val keyCompleted = completedTasksSnapshot.key!!
                        val title: String = completedTasksSnapshot.child("Title").value.toString()
                        val detail: String = completedTasksSnapshot.child("Details").value.toString()
                        val date: String = completedTasksSnapshot.child("Date").value.toString()
                        val time: String = completedTasksSnapshot.child("Time").value.toString()
                        val repeat: String = completedTasksSnapshot.child("Repeat").value.toString()
                        val important: String = completedTasksSnapshot.child("Important").value.toString()
                        val tasksList = TasksList(keyCompleted,title.trim(),detail.trim(),date.trim(),time.trim(),repeat.trim(),important.trim())
                        mutableCompleteTaskList.add(tasksList)
                    }
                    continue
                }
                val title: String = taskSnapshot.child("Title").value.toString()
                val detail: String = taskSnapshot.child("Details").value.toString()
                val date: String = taskSnapshot.child("Date").value.toString()
                val time: String = taskSnapshot.child("Time").value.toString()
                val repeat: String = taskSnapshot.child("Repeat").value.toString()
                val important: String = taskSnapshot.child("Important").value.toString()
                val tasksList = TasksList(key,title.trim(),detail.trim(),date.trim(),time.trim(),repeat.trim(),important.trim())
                mutableTaskList.add(tasksList)
            }
            (data as MutableLiveData<List<TasksList>>).postValue(mutableTaskList)
            (dataCompleteTasks as MutableLiveData<List<TasksList>>).postValue(mutableCompleteTaskList)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(requireContext,error.message,Toast.LENGTH_SHORT).show()
        }

    }

    init {
        databaseReference.addValueEventListener(valueEventListener)
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
    }

    fun addInCompleteTasksList(tasksList: TasksList){
        val key = databaseReference.child("Completed Tasks").push()
        key.child("Title").setValue(tasksList.title)
        key.child("Details").setValue(tasksList.details)
        key.child("Date").setValue(tasksList.date)
        key.child("Time").setValue(tasksList.time)
        key.child("Repeat").setValue(tasksList.repeat)
        key.child("Important").setValue(tasksList.important)
        deleteTask(tasksList)
    }

    fun deleteCompletedTask(tasksList: TasksList) {
        databaseReference.child("Completed Tasks").child(tasksList.id).removeValue()
    }

    fun addInCompletedTasksListUndo(removedItem: TasksList) {
        val key = databaseReference.child("Completed Tasks").push()
        key.child("Title").setValue(removedItem.title)
        key.child("Details").setValue(removedItem.details)
        key.child("Date").setValue(removedItem.date)
        key.child("Time").setValue(removedItem.time)
        key.child("Repeat").setValue(removedItem.repeat)
        key.child("Important").setValue(removedItem.important)
    }

    fun renameImportantCompletedTasks(tasksList: TasksList){
        val important = if(tasksList.important == "true"){ "false" }  else{ "true" }
        databaseReference.child("Completed Tasks").child(tasksList.id).child("Important").setValue(important)
    }

    fun addInUnCompletedTasksListener(tasksList: TasksList) {
        val key = databaseReference.push()
        key.child("Title").setValue(tasksList.title)
        key.child("Details").setValue(tasksList.details)
        key.child("Date").setValue(tasksList.date)
        key.child("Time").setValue(tasksList.time)
        key.child("Repeat").setValue(tasksList.repeat)
        key.child("Important").setValue(tasksList.important)
        deleteCompletedTask(tasksList)
    }

    fun deleteAllTasks(){
        databaseReference.removeValue()
    }

    fun deleteCompletedList(){
        databaseReference.child("Completed Tasks").removeValue()
    }

    fun renameList(databaseReferencePrevious: DatabaseReference) {
        val dialog = dialog()

        val addTitle = dialog.findViewById<EditText>(R.id.addTitle)
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
}