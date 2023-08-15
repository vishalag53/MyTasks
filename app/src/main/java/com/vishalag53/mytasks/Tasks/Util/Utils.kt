package com.vishalag53.mytasks.Tasks.Util

import android.content.Context
import android.content.res.Resources
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.FragmentTasksBinding

fun setSortBtnFunction(binding: FragmentTasksBinding, resources: Resources, view: View, requireContext: Context) {
    binding.sortBtn.background = ResourcesCompat.getDrawable(resources, R.drawable.collapse_all_48,null)
    val popupMenu = PopupMenu(requireContext,view)
    popupMenu.inflate(R.menu.menu_overflow_sort)

    popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
        when (menuItem.itemId){
            R.id.dateAsc -> {
                TODO("DATE ASC")
                true
            }
            R.id.dateDesc -> {
                TODO("DATE DESC")
                true
            }
            R.id.nameAsc -> {
                TODO("NAME ASC")
                true
            }
            R.id.nameDesc -> {
                TODO("NAME DESC")
                true
            }
            else -> false
        }
    }

    popupMenu.show()
}

fun menuDelete(view: View, context: Context) {
    val popupMenu = PopupMenu(context,view)
    popupMenu.inflate(R.menu.menu_overflow_delete)

    popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
        when (menuItem.itemId){
            R.id.deleteAllTasks -> {
                TODO("DELETE ALL TASKS")
                true
            }
            else -> false
        }
    }

    popupMenu.show()
}

fun menuDoubleDelete(view: View, context: Context) {
    val popupMenu = PopupMenu(context,view)
    popupMenu.inflate(R.menu.menu_overflow_temporary)

    popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
        when (menuItem.itemId){
            R.id.deleteAllTasks -> {
                TODO("DELETE ALL TASK")
                true
            }
            R.id.deleteAllCompleteTasks -> {
                TODO("DELETE ALL COMPLETE TASKS")
                true
            }
            else -> false
        }
    }

    popupMenu.show()
}