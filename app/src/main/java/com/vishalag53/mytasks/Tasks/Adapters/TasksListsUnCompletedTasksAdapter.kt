package com.vishalag53.mytasks.Tasks.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.data.TasksList
import com.vishalag53.mytasks.databinding.TasksListItemBinding

class TasksListsUnCompletedTasksAdapter (
    private val requireContext: Context,
    private val taskListsClickListener: (TasksList) -> Unit,
    private val importantClickListener: (TasksList) -> Unit,
    private val completeTasksClickListener: (TasksList) -> Unit
) : ListAdapter<TasksList, TasksListsUnCompletedTasksAdapter.TasksListsViewHolder>(TasksListDiffUtil){

    companion object TasksListDiffUtil: DiffUtil.ItemCallback<TasksList>(){
        override fun areItemsTheSame(oldItem: TasksList, newItem: TasksList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TasksList, newItem: TasksList): Boolean {
            return oldItem == newItem
        }
    }

    class TasksListsViewHolder(val binding: TasksListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): TasksListsViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TasksListItemBinding.inflate(layoutInflater,parent,false)
                return TasksListsViewHolder(binding)
            }
        }
    }

    fun setFilteredList(filteredList: ArrayList<TasksList>){
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListsViewHolder {
        return TasksListsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TasksListsViewHolder, position: Int) {
        val tasksList = getItem(position)
        holder.bind()
        holder.binding.title.text = tasksList.title
        holder.binding.title.setOnClickListener {
            taskListsClickListener(tasksList)
        }
        if(tasksList.repeat!!.isNotEmpty()){
            holder.binding.RepeatIcon.visibility = View.VISIBLE
        }
        if(tasksList.date!!.isNotEmpty() || tasksList.time!!.isNotEmpty()){
            holder.binding.notificationIcon.visibility = View.VISIBLE
        }
        if(tasksList.important == "true"){
            holder.binding.addImportants.background = ContextCompat.getDrawable(requireContext,R.drawable.baseline_star_24)
        }
        else{
            holder.binding.addImportants.background = ContextCompat.getDrawable(requireContext,R.drawable.baseline_star_outline_24)
        }
        holder.binding.addImportants.setOnClickListener {
            importantClickListener(tasksList)
        }
        holder.binding.checkCompleteButton.setButtonDrawable(R.drawable.radio_button_unchecked_32px)
        holder.binding.checkCompleteButton.setOnClickListener{
            holder.binding.checkCompleteButton.setButtonDrawable(R.drawable.check_circle_32px)
            completeTasksClickListener(tasksList)
        }
    }
}