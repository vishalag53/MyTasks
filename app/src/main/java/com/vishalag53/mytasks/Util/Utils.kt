package com.vishalag53.mytasks.Util

import android.content.Context
import android.content.res.Resources
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.FragmentTasksBinding

fun setExpandBtnFunction(
    binding: FragmentTasksBinding,
    resources: Resources,
    view: View,
    requireContext: Context
) {
    binding.expendBtn.background = ResourcesCompat.getDrawable(resources, R.drawable.collapse_all_48,null)
    val popupMenu = PopupMenu(requireContext,view)
    popupMenu.inflate(R.menu.menu_overflow_expand)

    popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
        when (menuItem.itemId){
            R.id.createList -> {
                Toast.makeText(requireContext,"Create List",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.search -> {
                Toast.makeText(requireContext,"Search",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.sortBy -> {
                Toast.makeText(requireContext,"Sort By",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.dateAsc -> {
                Toast.makeText(requireContext,"Date Ascending",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.dateDesc -> {
                Toast.makeText(requireContext,"Date Descending",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.nameAsc -> {
                Toast.makeText(requireContext,"Name Ascending",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.nameDesc -> {
                Toast.makeText(requireContext,"Name Descending",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context,"Delete",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context,"Delete All",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.deleteAllCompleteTasks -> {
                Toast.makeText(context,"Delete Complete",Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    popupMenu.show()
}