package com.vishalag53.mytasks.Tasks.Repository

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.vishalag53.mytasks.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TasksListRepository(private val requireContext: Context) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun createTask(){

        var flagImportant = true
        var flagDetail = true

        val dialog = dialog()

        val addTitle = dialog.findViewById<EditText>(R.id.addTitle)
        val addImportant = dialog.findViewById<Button>(R.id.addImportant)
        val addDetail = dialog.findViewById<EditText>(R.id.addDetails)
        val showDetail = dialog.findViewById<Button>(R.id.showDetailEditText)
        val showCalendar = dialog.findViewById<Button>(R.id.showCalendar)
        val showTime =  dialog.findViewById<Button>(R.id.showTime)

        addTitle.hint = "New Task"

        showDetail.setOnClickListener {
            if(flagDetail){
                addDetail.visibility = View.VISIBLE
                flagDetail = false
            }
            else{
                addDetail.visibility = View.GONE
                flagDetail = true
            }
        }

        showCalendar.setOnClickListener { showDatePicker() }
        showTime.setOnClickListener { showTimePicker() }

        addImportant.setOnClickListener {
            if(flagImportant){
                addImportant.background = ContextCompat.getDrawable(requireContext,R.drawable.baseline_star_24)
                flagImportant = false
            }
            else if(!flagImportant){
                addImportant.background = ContextCompat.getDrawable(requireContext,R.drawable.baseline_star_outline_24)
                flagImportant = true
            }
        }

        dialogExtracted(dialog)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val currentDateTime = Calendar.getInstance()

        val datePicker = DatePickerDialog(requireContext,{ _, year, monthOfYear,dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,monthOfYear,dayOfMonth)

            val formattedDate = SimpleDateFormat("E, MMM d", Locale.getDefault()).format(selectedDate.time)
        },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun showTimePicker() {
        val selectedDate = Calendar.getInstance()
        val timePicker = TimePickerDialog(requireContext,{ _, hourOfDay, minute ->
            selectedDate.set(Calendar.HOUR_OF_DAY,hourOfDay)
            selectedDate.set(Calendar.MINUTE,minute)

            val formattedTime = SimpleDateFormat("h:mm a",Locale.getDefault()).format(selectedDate.time)
        },
            selectedDate.get(Calendar.HOUR_OF_DAY),
            selectedDate.get(Calendar.MINUTE),
            false
        )

        timePicker.show()
    }

    private fun dialog(): Dialog {
        val dialog = Dialog(requireContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_box_layout)
        dialog.findViewById<EditText>(R.id.addDetails).visibility = View.GONE
        return dialog
    }

    private fun dialogExtracted(dialog: Dialog) {
        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

}