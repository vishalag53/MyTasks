package com.vishalag53.mytasks.Tasks.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.TaskItemBinding

class TasksAdapter(
    private val taskClickListener: (NameList) -> Unit
): ListAdapter<NameList,TasksAdapter.TasksListViewHolder>(NameListDiffCallback()) {
    class TasksListViewHolder(val binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): TasksListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                return TasksListViewHolder(binding)
            }
        }
    }

    fun setFilteredList(filteredList: ArrayList<NameList>) {
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder {
        return TasksListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int) {
        val nameList = getItem(position)
        holder.bind()
        holder.binding.tvListName.text = nameList.listNameName
        holder.binding.tvTotalTasks.text = nameList.totalTasks.toString()
        holder.binding.clListItem.setOnClickListener {
            taskClickListener(nameList)
        }

    }
}

class NameListDiffCallback: DiffUtil.ItemCallback<NameList>() {
    override fun areItemsTheSame(oldItem: NameList, newItem: NameList): Boolean {
        return oldItem.listNameId == newItem.listNameId
    }

    override fun areContentsTheSame(oldItem: NameList, newItem: NameList): Boolean {
        return oldItem == newItem
    }
}