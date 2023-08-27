package com.vishalag53.mytasks.Tasks.Util

import android.app.Dialog
import android.content.Context
import android.view.Window
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.Adapters.MoveToAdapters
import com.vishalag53.mytasks.Tasks.TasksFragment.TasksFragmentDirections
import com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment.TasksListsDetailsFragmentDirections
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.Tasks.data.TasksList

class MoveTasksList(
    private val tasksListName: MutableList<NameList>,
    private val requireContext: Context,
    private val name: String,
    private val databaseReferenceStarting: DatabaseReference,
    private val argumentsTaskList: TasksList,
    private val argumentsNameList: NameList,
    private val navController: NavController,
    private val argumentsTasksListName: MutableList<NameList>,
    ) {
    private lateinit var dialog : Dialog
    fun moveTasksList(){
        dialog = Dialog(requireContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_move_task_to)

        val moveToAdapter = MoveToAdapters(name,::moveToItem)
        moveToAdapter.submitList(tasksListName)

        val moveToRecyclerView = dialog.findViewById<RecyclerView>(R.id.rvMoveTaskToLists)

        moveToRecyclerView.adapter = moveToAdapter

        dialogTasksBelow(dialog)
    }

    private fun moveToItem(nameList: NameList){
        dialog.dismiss()
        val key = databaseReferenceStarting.child(nameList.listNameId).child("Tasks Lists").push()
        key.child("Title").setValue(argumentsTaskList.title)
        key.child("Details").setValue(argumentsTaskList.details)
        key.child("Date").setValue(argumentsTaskList.date)
        key.child("Time").setValue(argumentsTaskList.time)
        key.child("Repeat").setValue(argumentsTaskList.repeat)
        key.child("Important").setValue(argumentsTaskList.important)
        key.child("Completed").setValue(argumentsTaskList.isCompleted)

        databaseReferenceStarting.child(argumentsNameList.listNameId).child("Tasks Lists").child(argumentsTaskList.id).removeValue()

        navController.navigate(TasksListsDetailsFragmentDirections.actionTasksListDetailsFragmentToTasksFragment2())
        navController.navigate(TasksFragmentDirections.actionTasksFragmentToTasksListsFragment(argumentsNameList,argumentsTasksListName.toTypedArray()))
    }
}