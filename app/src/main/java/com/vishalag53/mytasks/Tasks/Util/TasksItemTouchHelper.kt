package com.vishalag53.mytasks.Tasks.Util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.mytasks.Tasks.Adapters.TasksFragmentAdapter
import com.vishalag53.mytasks.Tasks.TasksFragment.TasksFragmentViewModel
import com.vishalag53.mytasks.Tasks.data.NameList
import java.util.Collections

@Suppress("DEPRECATION")
class TasksItemTouchHelper(
    private val requireContext: Context,
    private val tasksAdapter: TasksFragmentAdapter,
    private val tasksViewModel: TasksFragmentViewModel,
    private val deleteIcon: Drawable,
    viewLifecycleOwner: LifecycleOwner
) : ItemTouchHelper.Callback() {

    private lateinit var mutableNameList: List<NameList>
    private lateinit var removedItem: String
    private var removedPosition: Int = -1

    init {
        tasksViewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableNameList = it
            }
        })
    }

    private val background = ColorDrawable(Color.RED)

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags,swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        if(toPosition >= 0 && toPosition < mutableNameList.size) Collections.swap(mutableNameList,fromPosition,toPosition)
        tasksAdapter.notifyItemMoved(fromPosition,toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        removedItem = mutableNameList[position-1].listNameName
        removedPosition = position - 1
        tasksViewModel.getDelete(mutableNameList[position-1])

    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight)/2
        val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight)/2
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        when{
            dX > 0 -> {
                val iconLeft = itemView.left +  iconMargin
                val iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth
                deleteIcon.setBounds(iconLeft,iconTop,iconRight,iconBottom)
                background.setBounds(itemView.left,itemView.top,itemView.left + dX.toInt(),itemView.bottom)
            }
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