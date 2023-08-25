package com.vishalag53.mytasks.Tasks.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.vishalag53.mytasks.R

// Dialog box for creating the tasks lists
fun dialogTasksLists(requireContext: Context): Dialog {
    val dialog = Dialog(requireContext)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.create_dialog_box)

    dialog.findViewById<EditText>(R.id.addDetails).visibility = View.GONE
    dialog.findViewById<Button>(R.id.showDetailEditText).visibility = View.GONE
    dialog.findViewById<Button>(R.id.showCalendar).visibility = View.GONE
    dialog.findViewById<Button>(R.id.showTime).visibility = View.GONE
    dialog.findViewById<Button>(R.id.showRepeat).visibility = View.GONE
    dialog.findViewById<Button>(R.id.addImportant).visibility = View.GONE
    dialog.findViewById<ConstraintLayout>(R.id.showDateTimeRepeatDetail).visibility = View.GONE
    dialog.findViewById<ConstraintLayout>(R.id.clRemind).visibility = View.GONE

    return dialog
}

fun dialogTasksListsBelow(dialog: Dialog) {
    dialog.show()
    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    dialog.window!!.setGravity(Gravity.BOTTOM)
}

// Dialog box for creating the tasks

fun dialogTasks(requireContext: Context): Dialog {
    val dialog = Dialog(requireContext)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.create_dialog_box)
    dialog.findViewById<EditText>(R.id.addDetails).visibility = View.GONE
    return dialog
}

fun dialogTasksBelow(dialog: Dialog) {
    dialog.show()
    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    dialog.window!!.setGravity(Gravity.BOTTOM)
}

// Dialog for repeat

fun dialogRepeat(requireContext: Context): Dialog {
    val dialog = Dialog(requireContext)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.repeat)
    dialog.show()
    return dialog
}

fun dialogRepeatBelow(dialog: Dialog) {
    dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.setGravity(Gravity.CENTER)
}