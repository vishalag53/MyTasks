package com.vishalag53.mytasks.Tasks.Util

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.MenuInflater
import android.view.View
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

    private lateinit var dialog: Dialog
    private lateinit var addNumber: EditText
    private lateinit var selectMonthBtn : TextView
    private lateinit var sundayBtn : AppCompatButton
    private lateinit var mondayBtn : AppCompatButton
    private lateinit var tuesdayBtn : AppCompatButton
    private lateinit var wednesdayBtn : AppCompatButton
    private lateinit var thursdayBtn : AppCompatButton
    private lateinit var fridayBtn : AppCompatButton
    private lateinit var saturdayBtn : AppCompatButton
    private lateinit var doneBtn: TextView
    private lateinit var cancelBtn: TextView

    private var selectTimeInterval = "weeks"

    private var sunday = false
    private var monday = false
    private var tuesday = false
    private var wednesday = false
    private var thursday = false
    private var friday = false
    private var saturday = false

    private var flagSunday = true
    private var flagMonday = true
    private var flagTuesday = true
    private var flagWednesday = true
    private var flagThursday = true
    private var flagFriday = true
    private var flagSaturday = true

    @RequiresApi(Build.VERSION_CODES.N)
    fun createTask(){
        var flagImportant = true
        var flagDetail = true
        dialog = dialogTasks(requireContext)
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

        showCalendar.setOnClickListener {
            if(date.isNotEmpty()) setDateOnCalendar()
            else showDatePicker()
        }
        insideDate.setOnClickListener { setDateOnCalendar() }
        cancelDateBtn.setOnClickListener {
            showDateDetail.text = null
            insideDate.visibility = View.GONE
            date = ""
        }

        showTime.setOnClickListener {
            if(time.isNotEmpty()) setTimeOnCalendar()
            else showTimePicker()
        }
        insideTime.setOnClickListener { setTimeOnCalendar() }
        cancelTimeBtn.setOnClickListener {
            showTimeDetail.text = null
            insideTime.visibility = View.GONE
            time = ""
        }

        showRepeat.setOnClickListener {
            if(repeat.isNotEmpty()) setRepeat()
            else showRepeatDialog()
        }
        insideRepeat.setOnClickListener { setRepeat() }
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

            if(title.isNotEmpty()){
                addArrayInFirebase()

                addTitle.text = null
                addDetail.text = null
                showDateDetail.text = null
                showTimeDetail.text = null
                showRepeatDetail.text = null
                title = ""
                details = ""
                date = ""
                time = ""
                repeat = ""
                isImportant = "false"
                addImportant.background = ContextCompat.getDrawable(requireContext, R.drawable.baseline_star_outline_24)

                insideDate.visibility = View.GONE
                insideTime.visibility = View.GONE
                insideRepeat.visibility = View.GONE
            }
            else{
                Toast.makeText(requireContext,"Enter the task name!",Toast.LENGTH_SHORT).show()
            }
        }

        dialogTasksBelow(dialog)
    }

    private fun setRepeat() {
        val textRepeat = repeat
        val text = textRepeat.split(" ")
        when (text[0].trim()) {
            "Weekly" -> showRepeatWeeksDialog(1, text)
            "Yearly" -> showRepeatDialog(1, "year")
            "Monthly" -> showRepeatDialog(1, "month")
            "Daily" -> showRepeatDialog(1, "day")
        }
        if (text.size >= 3) when (text[2].trim()) {
            "years" -> showRepeatDialog(text[1].trim().toInt(), "year")
            "months" -> showRepeatDialog(text[1].trim().toInt(), "month")
            "days" -> showRepeatDialog(text[1].trim().toInt(), "day")
            "week" -> showRepeatWeeksDialog(text[1].trim().toInt(), text)
        }
    }

    private fun setTimeOnCalendar() {
        val txtTime = time
        val hours = getHours(txtTime)
        val minute = if (hours in 0..9) getMinute(txtTime, false)
        else getMinute(txtTime, true)
        val isPM = if (hours in 0..9) getPM(txtTime, false)
        else getPM(txtTime, true)
        if (isPM) showTimePickerAtSpecificTime(hours + 12, minute)
        else showTimePickerAtSpecificTime(hours, minute)
    }


    private fun addArrayInFirebase() {
        val key = databaseReference.push()
        key.child("Title").setValue(title)
        key.child("Details").setValue(details)
        key.child("Date").setValue(date)
        key.child("Time").setValue(time)
        key.child("Repeat").setValue(repeat)
        key.child("Important").setValue(isImportant)
        key.child("Completed").setValue("false")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val currentDateTime = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext,{ _, year, monthOfYear,dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,monthOfYear,dayOfMonth)
            getDate(selectedDate)
        },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDateOnCalendar() {
        val textDate = date
        val days = getDay(textDate)
        val year = if (days in 0..9) getYear(textDate, false)
        else getYear(textDate, true)
        val month = getMonth(textDate)
        showDatePickerAtSpecificDate(days, month, year)
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

    private fun showTimePicker() {
        val selectedDate = Calendar.getInstance()
        val timePicker = TimePickerDialog(requireContext,{ _, hourOfDay, minute ->
            selectedDate.set(Calendar.HOUR_OF_DAY,hourOfDay)
            selectedDate.set(Calendar.MINUTE,minute)

            getTime(selectedDate)
        },
            selectedDate.get(Calendar.HOUR_OF_DAY),
            selectedDate.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerAtSpecificDate(days: Int, month: Int, years: Int) {
        val datePicker = DatePickerDialog(requireContext,{ _, year,monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,monthOfYear,dayOfMonth)
            getDate(selectedDate)
        },
            years,
            month-1,
            days
        )
        datePicker.show()
    }

    private fun getDate(selectedDate: Calendar) {
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
            getTime(selectedTime)
        },
            hours,
            minute,
            false
        )
        timePicker.show()
    }

    private fun getTime(selectedDate: Calendar) {
        time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(selectedDate.time)
        insideTime.visibility = View.VISIBLE
        showTimeDetail.text = time
    }

    private fun showRepeatDialog() {
        dialog = dialogRepeat(requireContext)
        selectTimeInterval = "weeks"
        repeat(dialog)
        dialogRepeatBelow(dialog)
    }

    private fun showRepeatDialog(number: Int,timeInterval: String){
        dialog = dialogRepeat(requireContext)
        dialog.findViewById<ConstraintLayout>(R.id.days).visibility = View.GONE
        initializedTimeInterValAndNumber()
        addNumber.setText("$number")

        when (timeInterval) {
            "day" -> {
                selectMonthBtn.setText(R.string.days)
                selectTimeInterval = "days"
            }
            "month" -> {
                selectMonthBtn.setText(R.string.months)
                selectTimeInterval = "months"
            }
            "year" -> {
                selectMonthBtn.setText(R.string.years)
                selectTimeInterval = "years"
            }
        }
        repeat(dialog)
        dialogRepeatBelow(dialog)
    }

    private fun showRepeatWeeksDialog(number: Int, days: List<String>) {
        dialog = dialogRepeat(requireContext)
        dialog.findViewById<ConstraintLayout>(R.id.days).visibility = View.VISIBLE
        initializedTimeInterValAndNumber()
        initializedDaysButton()

        addNumber.setText("$number")
        selectMonthBtn.setText(R.string.weeks)

        val days1 : String
        val days2 : String
        val days3 : String
        val days4 : String
        val days5 : String
        val days6 : String
        val days7 : String

        if(days[0] == "Weekly"){
            days1 = days[1].trim()
            days2 = if(days.size >= 3)   days[2].trim()  else ""
            days3 = if(days.size >= 4)   days[3].trim()  else ""
            days4 = if(days.size >= 5)   days[4].trim()  else ""
            days5 = if(days.size >= 6)   days[5].trim()  else ""
            days6 = if(days.size >= 7)   days[6].trim()  else ""
            days7 = if(days.size == 8)   days[7].trim()  else ""
        }
        else{
            days1 = days[3].trim()
            days2 = if(days.size >= 5)   days[4].trim()  else ""
            days3 = if(days.size >= 6)   days[5].trim()  else ""
            days4 = if(days.size >= 7)   days[6].trim()  else ""
            days5 = if(days.size >= 8)   days[7].trim()  else ""
            days6 = if(days.size >= 9)   days[8].trim()  else ""
            days7 = if(days.size == 10)  days[9].trim()  else ""
        }

        if(days1 == "SUN") showSelectedSundayBtn()

        when ("MON"){
            days1 -> showSelectedMondayBtn()
            days2 -> showSelectedMondayBtn()
        }

        when ("TUE"){
            days1 -> showSelectedTuesdayBtn()
            days2 -> showSelectedTuesdayBtn()
            days3 -> showSelectedTuesdayBtn()
        }

        when ("WED"){
            days1 -> showSelectedWednesdayBtn()
            days2 -> showSelectedWednesdayBtn()
            days3 -> showSelectedWednesdayBtn()
            days4 -> showSelectedWednesdayBtn()
        }

        when ("THU"){
            days1 -> showSelectedThursdayBtn()
            days2 -> showSelectedThursdayBtn()
            days3 -> showSelectedThursdayBtn()
            days4 -> showSelectedThursdayBtn()
            days5 -> showSelectedThursdayBtn()
        }

        when ("FRI"){
            days1 -> showSelectedFridayBtn()
            days2 -> showSelectedFridayBtn()
            days3 -> showSelectedFridayBtn()
            days4 -> showSelectedFridayBtn()
            days5 -> showSelectedFridayBtn()
            days6 -> showSelectedFridayBtn()
        }

        when ("SAT"){
            days1 -> showSelectedSaturdayBtn()
            days2 -> showSelectedSaturdayBtn()
            days3 -> showSelectedSaturdayBtn()
            days4 -> showSelectedSaturdayBtn()
            days5 -> showSelectedSaturdayBtn()
            days6 -> showSelectedSaturdayBtn()
            days7 -> showSelectedSaturdayBtn()
        }

        selectTimeInterval = "weeks"
        repeat(dialog)
        dialogRepeatBelow(dialog)
    }

    private fun repeat(dialog: Dialog) {
        val selectBtn = dialog.findViewById<ConstraintLayout>(R.id.select)
        val days = dialog.findViewById<ConstraintLayout>(R.id.days)
        cancelBtn = dialog.findViewById(R.id.cancel)
        doneBtn = dialog.findViewById(R.id.done)

        initializedTimeInterValAndNumber()
        initializedDaysButton()

        selectBtn.setOnClickListener {
            val popupMenu = PopupMenu(requireContext, it)
            val inflater: MenuInflater = popupMenu.menuInflater

            inflater.inflate(R.menu.menu_time_intervals, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.daysMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.days)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.GONE
                        true
                    }
                    R.id.weeksMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.weeks)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.VISIBLE
                        true
                    }
                    R.id.monthsMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.months)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.GONE
                        true
                    }
                    R.id.yearsMenu -> {
                        selectTimeInterval = requireContext.getString(R.string.years)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.GONE
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        sundayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagSunday = if (flagSunday) {
                    sunday = true
                    sundayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    sundayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    sunday = false
                    sundayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    sundayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagSunday = if (flagSunday) {
                    sunday = true
                    sundayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    sundayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    sunday = false
                    sundayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    sundayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        mondayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagMonday = if (flagMonday) {
                    monday = true
                    mondayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    mondayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    monday = false
                    mondayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    mondayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagMonday = if (flagMonday) {
                    monday = true
                    mondayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    mondayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    monday = false
                    mondayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    mondayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        tuesdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagTuesday = if (flagTuesday) {
                    tuesday = true
                    tuesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    tuesdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    tuesday = false
                    tuesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    tuesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagTuesday = if (flagTuesday) {
                    tuesday = true
                    tuesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    tuesdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    tuesday = false
                    tuesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    tuesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        wednesdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagWednesday = if (flagWednesday) {
                    wednesday = true
                    wednesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    wednesdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    wednesday = false
                    wednesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    wednesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagWednesday = if (flagWednesday) {
                    wednesday = true
                    wednesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    wednesdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    wednesday = false
                    wednesdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    wednesdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        thursdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagThursday = if (flagThursday) {
                    thursday = true
                    thursdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    thursdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    thursday = false
                    thursdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    thursdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagThursday = if (flagThursday) {
                    thursday = true
                    thursdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    thursdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    thursday = false
                    thursdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    thursdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        fridayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagFriday = if (flagFriday) {
                    friday = true
                    fridayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    fridayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    friday = false
                    fridayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    fridayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagFriday = if (flagFriday) {
                    friday = true
                    fridayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    fridayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    friday = false
                    fridayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    fridayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        saturdayBtn.setOnClickListener {
            val configuration = requireContext.resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flagSaturday = if (flagSaturday) {
                    saturday = true
                    saturdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
                    saturdayBtn.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    saturday = false
                    saturdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_night)
                    saturdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flagSaturday = if (flagSaturday) {
                    saturday = true
                    saturdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
                    saturdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
                    false
                } else {
                    saturday = false
                    saturdayBtn.setBackgroundResource(R.drawable.weeks_rounded_btn_day)
                    saturdayBtn.setTextColor(Color.parseColor("#FF9C9C9C"))
                    true
                }
            }
        }

        doneBtn.setOnClickListener {
            val number = addNumber.text.toString()
            if (number.isNotEmpty() && number != "0") {
                if (selectTimeInterval == "weeks") {
                    if (sunday || monday || tuesday || wednesday || thursday || friday || saturday) {
                        if (number.toInt() == 1) {
                            repeat = "Weekly"
                            if (sunday) repeat += " SUN"
                            if (monday) repeat += " MON"
                            if (tuesday) repeat += " TUE"
                            if (wednesday) repeat += " WED"
                            if (thursday) repeat += " THU"
                            if (friday) repeat += " FRI"
                            if (saturday) repeat += " SAT"
                        } else {
                            repeat = "Every $number week"
                            if (sunday) repeat += " SUN"
                            if (monday) repeat += " MON"
                            if (tuesday) repeat += " TUE"
                            if (wednesday) repeat += " WED"
                            if (thursday) repeat += " THU"
                            if (friday) repeat += " FRI"
                            if (saturday) repeat += " SAT"
                        }
                    } else {
                        Toast.makeText(requireContext, "Select minimum one Days", Toast.LENGTH_SHORT).show()
                    }
                } else if (selectTimeInterval == "days") {
                    repeat = if (number.toInt() == 1) "Daily"
                    else "Every $number days"
                } else if (selectTimeInterval == "months") {
                    repeat = if (number.toInt() == 1) "Monthly"
                    else "Every $number months"
                } else if (selectTimeInterval == "years") {
                    repeat = if (number.toInt() == 1) "Yearly"
                    else "Every $number years"
                }

                insideRepeat.visibility = View.VISIBLE
                showRepeatDetail.text = repeat
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext, "Enter the number", Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun initializedTimeInterValAndNumber() {
        addNumber = dialog.findViewById(R.id.number)
        selectMonthBtn = dialog.findViewById(R.id.selectTimeIntervals)
    }

    private fun initializedDaysButton(){
        sundayBtn = dialog.findViewById(R.id.sunday)
        mondayBtn = dialog.findViewById(R.id.monday)
        tuesdayBtn = dialog.findViewById(R.id.tuesday)
        wednesdayBtn = dialog.findViewById(R.id.wednesday)
        thursdayBtn = dialog.findViewById(R.id.thursday)
        fridayBtn = dialog.findViewById(R.id.friday)
        saturdayBtn = dialog.findViewById(R.id.saturday)
    }

    private fun showSelectedSundayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            sundayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            sundayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            sundayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            sundayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagSunday = false
        sunday = true
    }

    private fun showSelectedMondayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            mondayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            mondayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            mondayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            mondayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagMonday = false
        monday = true
    }

    private fun showSelectedTuesdayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            tuesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            tuesdayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            tuesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            tuesdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagTuesday = false
        tuesday = true
    }

    private fun showSelectedWednesdayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            wednesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            wednesdayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            wednesdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            wednesdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagWednesday = false
        wednesday = true
    }

    private fun showSelectedThursdayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            thursdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            thursdayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            thursdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            thursdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagThursday = false
        thursday = true
    }

    private fun showSelectedFridayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            fridayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            fridayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            fridayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            fridayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagFriday = false
        friday = true
    }

    private fun showSelectedSaturdayBtn(){
        val configuration = requireContext.resources.configuration
        if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            saturdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_white)
            saturdayBtn.setTextColor(Color.parseColor("#FF000000"))
        } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            saturdayBtn.setBackgroundResource(R.drawable.rounded_bg_oval_blue)
            saturdayBtn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        flagSaturday = false
        saturday = true
    }
}