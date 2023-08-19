package com.vishalag53.mytasks.Tasks.Util

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DatabaseReference
import com.vishalag53.mytasks.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TasksListCreateButtonAction(
    private val requireContext: Context,
    private val databaseReference: DatabaseReference
) {

    private var tasksList : MutableList<String> = mutableListOf()

    private var title = ""
    private var details = ""
    private var date = ""
    private var time = ""
    private var repeat = ""
    private var isImportant = "false"

    private lateinit var insideDate : ConstraintLayout
    private lateinit var insideTime : ConstraintLayout
    private lateinit var insideRepeat : ConstraintLayout
    private lateinit var showDateDetail : TextView
    private lateinit var showTimeDetail : TextView
    private lateinit var showRepeatDetail : TextView
    private lateinit var cancelDateBtn : Button
    private lateinit var cancelTimeBtn : Button
    private lateinit var cancelRepeatBtn : Button
    private lateinit var setDueDateBtn : ConstraintLayout
    private lateinit var iconRemindBtn : Button

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
        val showRepeat = dialog.findViewById<Button>(R.id.showRepeat)
        val saveBtn = dialog.findViewById<Button>(R.id.save)

        insideDate = dialog.findViewById(R.id.insideDate)
        insideTime = dialog.findViewById(R.id.insideTime)
        insideRepeat = dialog.findViewById(R.id.insideRepeat)
        showDateDetail = dialog.findViewById(R.id.showDateDetail)
        showTimeDetail = dialog.findViewById(R.id.showTimeDetail)
        showRepeatDetail = dialog.findViewById(R.id.showRepeatDetail)
        cancelDateBtn = dialog.findViewById(R.id.cancelDateBtn)
        cancelTimeBtn = dialog.findViewById(R.id.cancelTimeBtn)
        cancelRepeatBtn = dialog.findViewById(R.id.cancelRepeatBtn)
        setDueDateBtn = dialog.findViewById(R.id.clRemind)
        iconRemindBtn = dialog.findViewById(R.id.iconRemind)

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

        setDueDateBtn.setOnClickListener {
            setDueDateBtn.visibility = View.GONE
            showCalendar.visibility = View.VISIBLE
            showTime.visibility = View.VISIBLE
        }

        iconRemindBtn.setOnClickListener {
            setDueDateBtn.visibility = View.GONE
            showCalendar.visibility = View.VISIBLE
            showTime.visibility = View.VISIBLE
        }

        showCalendar.setOnClickListener { showDatePicker() }
        insideDate.setOnClickListener {
            val textDate = showDateDetail.text.toString()
            val days = getDay(textDate)
            val year = if(days in 0..9) getYear(textDate,false)
                            else getYear(textDate,true)
            val month = getMonth(textDate)
            showDatePickerAtSpecificDate(days,month,year)
        }
        cancelDateBtn.setOnClickListener {
            showDateDetail.text = null
            insideDate.visibility = View.GONE
            date = ""
        }

        showTime.setOnClickListener { showTimePicker() }
        insideTime.setOnClickListener {
            val txtDate = showTimeDetail.text.toString()
            val hours = getHours(txtDate)
            val minute = if(hours in 0 .. 9) getMinute(txtDate,false)
                                else getMinute(txtDate,true)
            val isPM = if(hours in 0 .. 9) getPM(txtDate,false)
                                else getPM(txtDate,true)
            if(isPM) showTimePickerAtSpecificTime(hours + 12 , minute)
            else showTimePickerAtSpecificTime(hours,minute)
        }
        cancelTimeBtn.setOnClickListener {
            showTimeDetail.text = null
            insideTime.visibility = View.GONE
            time = ""
        }

        showRepeat.setOnClickListener { showRepeatDialog() }
        insideRepeat.setOnClickListener { showRepeatDialog() }
        cancelRepeatBtn.setOnClickListener {
            showRepeatDetail.text = null
            insideRepeat.visibility = View.GONE
            repeat = ""
        }

        addImportant.setOnClickListener {
            if(flagImportant){
                isImportant = "true"
                addImportant.background = ContextCompat.getDrawable(requireContext, R.drawable.baseline_star_24)
                flagImportant = false
            }
            else{
                isImportant = "false"
                addImportant.background = ContextCompat.getDrawable(requireContext, R.drawable.baseline_star_outline_24)
                flagImportant = true
            }
        }

        saveBtn.setOnClickListener {
            title = addTitle.text.toString()
            details = addDetail.text.toString()

            tasksList.clear()
            if(title.isNotEmpty()){
                tasksList.add(0,title)
                tasksList.add(1,details)
                tasksList.add(2,date)
                tasksList.add(3,time)
                tasksList.add(4,repeat)
                tasksList.add(5,isImportant)

                addArrayInFirebase()

                addTitle.text = null
                addDetail.text = null
                showDateDetail.text = null
                showTimeDetail.text = null
                showRepeatDetail.text = null
                addImportant.background = ContextCompat.getDrawable(requireContext, R.drawable.baseline_star_outline_24)

                insideDate.visibility = View.GONE
                insideTime.visibility = View.GONE
                insideRepeat.visibility = View.GONE
            }
            else{
                Toast.makeText(requireContext,"Enter the task name!",Toast.LENGTH_SHORT).show()
            }
        }

        dialogExtracted(dialog)
    }

    private fun addArrayInFirebase() {
        databaseReference.push().child("lists").setValue(tasksList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val currentDateTime = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext,{ _, year, monthOfYear,dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,monthOfYear,dayOfMonth)

            setDate(selectedDate)
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

            setTime(selectedDate)
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
        dialog.setContentView(R.layout.create_dialog_box)
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

    private fun getDay(textDate: String): Int {
        val a = textDate.substring(9, 11)
        if (a.isDigitsOnly()) return a.toInt()
        return textDate.substring(9, 10).toInt()
    }

    private fun getYear(textDate: String, b: Boolean): Int {
        return when (b){
            true -> textDate.substring(13,17).toInt()
            else -> textDate.substring(12,16).toInt()
        }
    }

    private fun getMonth(textDate: String):Int {
        return when (textDate.substring(5,8)){
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerAtSpecificDate(days: Int, month: Int, year: Int) {
        val datePicker = DatePickerDialog(requireContext,{ _, year,monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,monthOfYear,dayOfMonth)
            setDate(selectedDate)
        },
            year,
            month-1,
            days
        )
        datePicker.show()
    }

    private fun setDate(selectedDate: Calendar) {
        date = SimpleDateFormat("E, MMM d, yyyy", Locale.getDefault()).format(selectedDate.time)
        insideDate.visibility = View.VISIBLE
        showDateDetail.text = date
    }

    private fun getHours(txtTime: String): Int{
        val a = txtTime.substring(0,2)
        if(a.isDigitsOnly()) return a.toInt()
        return txtTime.substring(0,1).toInt()
    }

    private fun getMinute(txtTime: String, b: Boolean): Int{
        return when (b){
            true -> txtTime.substring(3,5).toInt()
            else -> txtTime.substring(2,4).toInt()
        }
    }

    private fun getPM(txtTime: String, b: Boolean): Boolean{
        return when (b){
            true -> {
                txtTime.substring(6,8) == "PM"
            }
            else -> txtTime.substring(5,7) == "PM"
        }
    }

    private fun showTimePickerAtSpecificTime(hours: Int, minute: Int) {
        val timePicker = TimePickerDialog(requireContext,{ _, selectedHour,selectedMinute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY,selectedHour)
            selectedTime.set(Calendar.MINUTE,selectedMinute)
            setTime(selectedTime)
        },
            hours,
            minute,
            false
        )
        timePicker.show()
    }

    private fun setTime(selectedDate: Calendar) {
        time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(selectedDate.time)
        insideTime.visibility = View.VISIBLE
        showTimeDetail.text = time
    }

    private fun showRepeatDialog() {
        val dialog = Dialog(requireContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.repeat)
        dialog.show()

        val selectBtn = dialog.findViewById<ConstraintLayout>(R.id.select)
        val selectMonthBtn = dialog.findViewById<TextView>(R.id.selectTimeIntervals)
        val addNumber = dialog.findViewById<EditText>(R.id.number)
        val weeks = dialog.findViewById<ConstraintLayout>(R.id.weeks)
        val sundayBtn = dialog.findViewById<AppCompatButton>(R.id.sunday)
        val mondayBtn = dialog.findViewById<AppCompatButton>(R.id.monday)
        val tuesdayBtn = dialog.findViewById<AppCompatButton>(R.id.tuesday)
        val wednesdayBtn = dialog.findViewById<AppCompatButton>(R.id.wednesday)
        val thursdayBtn = dialog.findViewById<AppCompatButton>(R.id.thursday)
        val fridayBtn = dialog.findViewById<AppCompatButton>(R.id.friday)
        val saturdayBtn = dialog.findViewById<AppCompatButton>(R.id.saturday)
        val cancelBtn = dialog.findViewById<TextView>(R.id.cancel)
        val doneBtn = dialog.findViewById<TextView>(R.id.done)

        var selectTimeInterval = "weeks"
        var sunday = false
        var monday = false
        var tuesday = false
        var wednesday = false
        var thursday = false
        var friday = false
        var saturday = false

        var flagSunday = true
        var flagMonday = true
        var flagTuesday = true
        var flagWednesday = true
        var flagThursday = true
        var flagFriday = true
        var flagSaturday = true

        selectBtn.setOnClickListener {
            val popupMenu = PopupMenu(requireContext,it)
            val inflater: MenuInflater = popupMenu.menuInflater

            inflater.inflate(R.menu.menu_time_intervals,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId){
                    R.id.daysMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.days)
                        selectMonthBtn.text = selectTimeInterval
                        weeks.visibility = View.GONE
                        true
                    }
                    R.id.weeksMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.weeks)
                        selectMonthBtn.text = selectTimeInterval
                        weeks.visibility = View.VISIBLE
                        true
                    }
                    R.id.monthsMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.months)
                        selectMonthBtn.text = selectTimeInterval
                        weeks.visibility = View.GONE
                        true
                    }
                    R.id.yearsMenu ->{
                        selectTimeInterval = requireContext.getString(R.string.years)
                        selectMonthBtn.text = selectTimeInterval
                        weeks.visibility = View.GONE
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        sundayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagSunday = if(flagSunday){
                    sunday = true
                    sundayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    sundayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    sunday = false
                    sundayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    sundayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagSunday = if(flagSunday){
                    sunday = true
                    sundayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    sundayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    sunday = false
                    sundayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    sundayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        mondayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagMonday = if(flagMonday){
                    monday = true
                    mondayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    mondayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    monday = false
                    mondayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    mondayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagMonday = if(flagMonday){
                    monday = true
                    mondayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    mondayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    monday = false
                    mondayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    mondayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        tuesdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagTuesday = if(flagTuesday){
                    tuesday = true
                    tuesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    tuesdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    tuesday = false
                    tuesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    tuesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagTuesday = if(flagTuesday){
                    tuesday = true
                    tuesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    tuesdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    tuesday = false
                    tuesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    tuesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        wednesdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagWednesday = if(flagWednesday){
                    wednesday = true
                    wednesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    wednesdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    wednesday = false
                    wednesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    wednesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagWednesday = if(flagWednesday){
                    wednesday = true
                    wednesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    wednesdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    wednesday = false
                    wednesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    wednesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        thursdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagThursday = if(flagThursday){
                    thursday = true
                    thursdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    thursdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    thursday = false
                    thursdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    thursdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagThursday = if(flagThursday){
                    thursday = true
                    thursdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    thursdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    thursday = false
                    thursdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    thursdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        fridayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagFriday = if(flagFriday){
                    friday = true
                    fridayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    fridayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    friday = false
                    fridayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    fridayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagFriday = if(flagFriday){
                    friday = true
                    fridayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    fridayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    friday = false
                    fridayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    fridayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        saturdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                flagSaturday = if(flagSaturday){
                    saturday = true
                    saturdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    saturdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else{
                    saturday = false
                    saturdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    saturdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
            else if(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                flagSaturday = if(flagSaturday){
                    saturday = true
                    saturdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    saturdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else{
                    saturday = false
                    saturdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    saturdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }


        doneBtn.setOnClickListener {
            val number = addNumber.text.toString()
            if(number.isNotEmpty() ){
                if(selectTimeInterval == "weeks"){
                    if(sunday || monday || tuesday || wednesday || thursday || friday || saturday){
                        if(number.toInt() == 1){
                            repeat = "Weekly \n"
                            if(sunday)  repeat += " SUN"
                            if(monday)  repeat += " MON"
                            if(tuesday)  repeat += " TUE"
                            if(wednesday)  repeat += " WED"
                            if(thursday)  repeat += " THU"
                            if(friday)  repeat += " FRI"
                            if(saturday)  repeat += " SAT"
                        }
                        else{
                            repeat = "Every $number week \n"
                            if(sunday)  repeat += " SUN"
                            if(monday)  repeat += " MON"
                            if(tuesday)  repeat += " TUE"
                            if(wednesday)  repeat += " WED"
                            if(thursday)  repeat += " THU"
                            if(friday)  repeat += " FRI"
                            if(saturday)  repeat += " SAT"
                        }
                    }
                    else{
                        Toast.makeText(requireContext,"Select minimum one Days",Toast.LENGTH_SHORT).show()
                    }
                }
                else if(selectTimeInterval == "days"){
                    repeat = if(number.toInt() == 1) "Daily"
                    else "Every $number days"
                }
                else if(selectTimeInterval == "months"){
                    repeat = if(number.toInt() == 1) "Monthly"
                    else "Every $number months"
                }
                else if(selectTimeInterval == "years"){
                    repeat = if(number.toInt() == 1) "Yearly"
                    else "Every $number years"
                }

                insideRepeat.visibility = View.VISIBLE
                showRepeatDetail.text = repeat
                dialog.dismiss()
            }
            else{
                Toast.makeText(requireContext,"Enter the number",Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.CENTER)
    }
}