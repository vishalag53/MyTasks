package com.vishalag53.mytasks.Tasks.Util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vishalag53.mytasks.Tasks.Adapters.TasksListsCompletedTasksAdapter
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.Tasks.TasksListsFragment.TasksListsViewModel
import com.vishalag53.mytasks.Tasks.data.TasksList

@Suppress("DEPRECATION")
class TasksListsItemCompletedTasksTouchHelper(
    private val requireContext: Context,
    private val tasksListsCompletedTasksAdapter: TasksListsCompletedTasksAdapter,
    private val tasksListsViewModel: TasksListsViewModel,
    private val deleteIcon: Drawable,
    viewLifecycleOwner: LifecycleOwner,
    private val tasksListsRepository: TasksListRepository,
    private val requireActivity: FragmentActivity
) : ItemTouchHelper.Callback() {

    private lateinit var mutableNameList: List<TasksList>

    init {
        tasksListsViewModel.dataCompletedTasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableNameList = it.reversed()
            }
        })
    }

    private val background = ColorDrawable(Color.RED)

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(0,swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val removedItem = mutableNameList[position]
        tasksListsRepository.deleteCompletedTask(mutableNameList[position])
        val rootView = requireActivity.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(rootView,"Do you want to Undo?", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            tasksListsRepository.addInCompletedTasksListUndo(removedItem)
        }
        snackbar.show()
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight)/2
        val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight)/2
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        when{
            dX < 0 -> {
                val iconLeft = itemView.right -  iconMargin - deleteIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                deleteIcon.setBounds(iconLeft,iconTop,iconRight,iconBottom)
                background.setBounds(itemView.right + dX.toInt(),itemView.top,itemView.right,itemView.bottom)
            }
            else -> {
                background.setBounds(0,0,0,0)
                deleteIcon.setBounds(0,0,0,0)
            }
        }

        background.draw(c)
        deleteIcon.draw(c)
    }
}