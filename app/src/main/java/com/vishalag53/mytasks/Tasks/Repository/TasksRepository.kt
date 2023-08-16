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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.TasksFragment.CacheManager
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
         val dialog = dialog()

         val addTitle = dialog.findViewById<EditText>(R.id.addTitle)
         addTitle.hint = "New Task List"

         val save = dialog.findViewById<TextView>(R.id.save)
         save.setTextColor(ContextCompat.getColor(requireContext,R.color.bkg_blue))
         save.setOnClickListener {
             val listName = addTitle.text.toString()
             if(listName.isNotEmpty()){
                 databaseReference.push().setValue(listName).addOnCompleteListener {
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
         dialogExtracted(dialog)
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
        val dialog = dialog()

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
        dialogExtracted(dialog)
    }

    private fun dialog(): Dialog {
        val dialog = Dialog(requireContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_box_layout)

        dialog.findViewById<EditText>(R.id.addDetails).visibility = View.GONE
        dialog.findViewById<Button>(R.id.showDetailEditText).visibility = View.GONE
        dialog.findViewById<Button>(R.id.showCalendarTime).visibility = View.GONE
        dialog.findViewById<Button>(R.id.addImportant).visibility = View.GONE
        return dialog
    }

    private fun dialogExtracted(dialog: Dialog) {
        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
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

    fun fetchData(): List<NameList>{
        var mutableNameList1: MutableList<NameList> = mutableListOf()

        if(mutableNameList.size > 15){
            var cnt = 0
            for (nameList in mutableNameList){
                mutableNameList1.add(nameList)
                cnt++
                if(cnt >= 15)   break
            }
            return mutableNameList1
        }
        else{
            return mutableNameList
        }
    }

}