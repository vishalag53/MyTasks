package com.vishalag53.mytasks.Tasks.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.HeaderTasksBinding
import com.vishalag53.mytasks.databinding.TaskListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
private var size: Int = 0

@Suppress("DEPRECATION")
class TasksFragmentAdapter(
    private val taskClickListener: (NameList) -> Unit,
    private val renameClickListener: (NameList) -> Unit
): ListAdapter<DataItem,RecyclerView.ViewHolder>(NameListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    class TasksListViewHolder(val binding: TaskListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): TasksListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskListBinding.inflate(layoutInflater, parent, false)
                return TasksListViewHolder(binding)
            }
        }
    }

    fun setFilteredList(filteredList: ArrayList<NameList>) {
        addHeaderAndSubmitList(filteredList)
    }

    fun addHeaderAndSubmitList(nameList: List<NameList>?){
        adapterScope.launch {
            if (nameList != null){
                size = nameList.size
            }
            val items = when (nameList){
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + nameList.map { DataItem.NameListItem(it) }
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)){
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.NameListItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> TasksListViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder){
            is TasksListViewHolder -> {
                val nameList = getItem(position) as DataItem.NameListItem
                holder.bind()

                holder.binding.textViewTitle3.text = nameList.nameList.listNameName

                holder.binding.clImport4.setOnClickListener {
                    taskClickListener(nameList.nameList)
                }

                holder.binding.renameBtn.setOnClickListener {
                    renameClickListener(nameList.nameList)
                }
            }
            is TextViewHolder -> {
                holder.bind()
                holder.binding.size.text = size.toString()
            }
        }
    }

    fun isHeaderPosition(position: Int) : Boolean{
        return getItemViewType(position) == ITEM_VIEW_TYPE_HEADER
    }
}

class NameListDiffCallback: DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}

sealed class  DataItem{
    data class NameListItem(val nameList: NameList) : DataItem(){
        override val id = nameList.listNameId
    }
    object Header: DataItem(){
        override val id = ""
    }

    abstract val id : String

}

class TextViewHolder(val binding: HeaderTasksBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(){
        binding.executePendingBindings()
    }
    companion object{
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)!!
            val binding = HeaderTasksBinding.inflate(layoutInflater,parent,false)
            return TextViewHolder(binding)
        }
    }
}