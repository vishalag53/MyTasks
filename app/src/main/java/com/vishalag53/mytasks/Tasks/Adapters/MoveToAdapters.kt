package com.vishalag53.mytasks.Tasks.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.ItemMoveToBinding

class MoveToAdapters(
    private val name: String,
    private val moveToItem : (NameList) -> Unit
): ListAdapter<NameList, MoveToAdapters.MoveToViewHolder>(MoveTODiffCallback()) {

    class MoveToViewHolder(val binding: ItemMoveToBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): MoveToViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMoveToBinding.inflate(layoutInflater,parent,false)
                return MoveToViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveToViewHolder {
        return MoveToViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MoveToViewHolder, position: Int) {
       val nameList = getItem(position)
        holder.bind()
        holder.binding.name.text = nameList.listNameName

        if(name == nameList.listNameName){
            holder.binding.imageTick.setImageResource(R.drawable.baseline_done_24)
        }

        holder.binding.imageTick.setOnClickListener{ moveToItem(nameList) }
        holder.binding.name.setOnClickListener{ moveToItem(nameList) }
    }

}

class MoveTODiffCallback: DiffUtil.ItemCallback<NameList>(){
    override fun areItemsTheSame(oldItem: NameList, newItem: NameList): Boolean {
        return oldItem.listNameId == newItem.listNameId
    }

    override fun areContentsTheSame(oldItem: NameList, newItem: NameList): Boolean {
        return  oldItem == newItem
    }

}