package com.vishalag53.mytasks.Tasks.TasksFragment

import android.app.AlertDialog
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.TaskListBinding

class TasksFragmentAdapter(private val nameList: MutableList<NameList>,
    private val deleteCallback: (NameList) -> Unit,
    private val renameCallback: (NameList,String) -> Unit): RecyclerView.Adapter<TasksFragmentAdapter.TasksListViewHolder>() {

    class TasksListViewHolder(val binding: TaskListBinding): RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener{

        init {
            binding.root.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val inflater: MenuInflater = MenuInflater(v?.context)
            inflater.inflate(R.menu.nenu_overflow_compulsory,menu)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder {
        return TasksListViewHolder(TaskListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int) {
        val item = nameList[position]
        holder.binding.textViewTitle3.text = item.listNameName

        holder.binding.menuBtn3.setOnClickListener {
            val popUp = PopupMenu(holder.itemView.context,it)
            val inflater: MenuInflater = popUp.menuInflater
            inflater.inflate(R.menu.nenu_overflow_compulsory,popUp.menu)

            popUp.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId){
                    R.id.renameListName -> {
                        showRenameDialog(nameList[position],holder.binding)
                        true
                    }
                    R.id.deleteList -> {
                        val deleteList = nameList[position]
                        deleteCallback(deleteList)
                        true
                    }
                    R.id.deleteAllTasks -> {
                        TODO("Delete All Tasks")
                        true
                    }
                    R.id.deleteAllCompleteTasks -> {
                        TODO("Delete All Complete Tasks")
                        true
                    }
                    else -> false
                }
            }

            popUp.show()

        }
    }

    private fun showRenameDialog(nameList: NameList, binding: TaskListBinding) {
        val dialogBox = AlertDialog.Builder(binding.root.context)
        val editText = EditText(binding.root.context)
        editText.setText(nameList.listNameName)

        dialogBox.setView(editText)
            .setTitle("Rename List")
            .setPositiveButton("Rename"){ _, _ ->
                val newName = editText.text.toString()
                renameCallback(nameList,newName)
            }
            .setNegativeButton("Cancel",null)
            .show()
    }

}