package com.vishalag53.mytasks.Tasks.Util

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.vishalag53.mytasks.R
import java.util.Calendar


class TasksListCreateButtonAction(
    private val requireContext: Context,
    private val databaseReference: DatabaseReference
) {
    // show task data
    private var title = ""
    private var details = ""
    private var date = ""
    private var time = ""
    private var repeat = ""
    private var isImportant = "false"

    // dialog create
    private lateinit var dialogCreate: Dialog
    private lateinit var insideRemindMe: ConstraintLayout
    private lateinit var showRemindMeDetail: TextView
    private lateinit var cancelRemindMeBtn: Button
    private lateinit var iconRemindMeBtn: Button

    // dialog remind me
    private lateinit var dialogRemindMe: Dialog
    private lateinit var clickTime: ConstraintLayout
    private lateinit var showTime: Button
    private lateinit var clickRepeat: ConstraintLayout
    private lateinit var showRepeat: Button
    private lateinit var datePicker: DatePicker
    private lateinit var setTime: TextView
    private lateinit var cancelTimeBtn: Button
    private lateinit var cancelRepeatBtn: Button
    private lateinit var setRepeat: TextView

    // dialog time
    private lateinit var dialogTime: Dialog

    // dialog repeat
    private lateinit var dialogRepeat: Dialog
    private lateinit var doneRepeatBtn: TextView
    private lateinit var addNumber: EditText
    private lateinit var selectMonthBtn: TextView
    private lateinit var cancelRepeat: TextView
    private lateinit var sundayBtn: AppCompatButton
    private lateinit var mondayBtn: AppCompatButton
    private lateinit var tuesdayBtn: AppCompatButton
    private lateinit var wednesdayBtn: AppCompatButton
    private lateinit var thursdayBtn: AppCompatButton
    private lateinit var fridayBtn: AppCompatButton
    private lateinit var saturdayBtn: AppCompatButton
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun createTask() {
        var flagImportant = true
        var flagDetail = true
        dialogCreate = dialogTasks(requireContext)
        val addTitle = dialogCreate.findViewById<EditText>(R.id.addTitle)!!
        val addImportant = dialogCreate.findViewById<Button>(R.id.addImportant)!!
        val addDetail = dialogCreate.findViewById<EditText>(R.id.addDetails)!!
        val cancelDetailBtn = dialogCreate.findViewById<Button>(R.id.cancelDetailBtn)!!
        val showDetail = dialogCreate.findViewById<Button>(R.id.showDetailEditText)!!
        val saveBtn = dialogCreate.findViewById<Button>(R.id.save)!!

        insideRemindMe = dialogCreate.findViewById(R.id.insideRemindMe)
        showRemindMeDetail = dialogCreate.findViewById(R.id.showRemindMe)
        cancelRemindMeBtn = dialogCreate.findViewById(R.id.cancelRemindMeDetailBtn)
        iconRemindMeBtn = dialogCreate.findViewById(R.id.iconRemind)

        addTitle.hint = "New Task"

        showDetail.setOnClickListener {
            if(flagDetail){
                addDetail.visibility = View.VISIBLE
                cancelDetailBtn.visibility = View.VISIBLE
                flagDetail = false
            }
            else{
                addDetail.visibility = View.GONE
                cancelDetailBtn.visibility = View.GONE
                flagDetail = true
            }
        }

        cancelDetailBtn.setOnClickListener {
            details = ""
            addDetail.text = Editable.Factory.getInstance().newEditable(details)
        }

        iconRemindMeBtn.setOnClickListener {
            openRemindMeDialogBox()
        }

        showRemindMeDetail.setOnClickListener {
            openRemindMeDialogBox()
        }

        cancelRemindMeBtn.setOnClickListener {
            showRemindMeDetail.text = null
            insideRemindMe.visibility = View.GONE
            time = ""
            date = ""
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //createNotificationChannel()
        }

        saveBtn.setOnClickListener {
            title = addTitle.text.toString()
            details = addDetail.text.toString()

            if(title.isNotEmpty()){
                addInFirebase()
                dialogCreate.dismiss()
                if(date.isNotEmpty() && time.isNotEmpty()){
                    //scheduleNotification()
                }

                addTitle.text = null
                addDetail.text = null
                showRemindMeDetail.text = null
                title = ""
                details = ""
                date = ""
                time = ""
                repeat = ""
                isImportant = "false"
                addImportant.background = ContextCompat.getDrawable(requireContext, R.drawable.baseline_star_outline_24)
                insideRemindMe.visibility = View.GONE
            }
            else{
                Toast.makeText(requireContext,"Enter the task name!", Toast.LENGTH_SHORT).show()
            }
        }

        dialogTasksBelow(dialogCreate)
    }


    // add in firebase
    private fun addInFirebase() {
        val key = databaseReference.push()
        key.child("Title").setValue(title)
        key.child("Details").setValue(details)
        key.child("Date").setValue(date)
        key.child("Time").setValue(time)
        key.child("Repeat").setValue(repeat)
        key.child("Important").setValue(isImportant)
        key.child("Completed").setValue("false")
    }

    // Remind me

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openRemindMeDialogBox(){
        dialogRemindMe = dialogRemindMe(requireContext)
        clickTime = dialogRemindMe.findViewById(R.id.clickTime)
        showTime = dialogRemindMe.findViewById(R.id.showTime)
        clickRepeat = dialogRemindMe.findViewById(R.id.clickRepeat)
        showRepeat = dialogRemindMe.findViewById(R.id.showRepeat)
        datePicker = dialogRemindMe.findViewById(R.id.datePicker)
        setTime = dialogRemindMe.findViewById(R.id.setTime)
        cancelTimeBtn = dialogRemindMe.findViewById(R.id.cancelTimeBtn)
        setRepeat = dialogRemindMe.findViewById(R.id.setRepeat)
        cancelRepeatBtn = dialogRemindMe.findViewById(R.id.cancelRepeatBtn)

        val cancelRemindMe = dialogRemindMe.findViewById<TextView>(R.id.cancelRemindMe)!!
        val doneRemindMe = dialogRemindMe.findViewById<TextView>(R.id.doneRemindMe)!!

        if(showRemindMeDetail.text != ""){
            val remindMeDetail = showRemindMeDetail.text.toString()
            val details = remindMeDetail.split('\n')

            val date = details[0]
            var time  = ""
            var repeat = ""

            if(details.size > 1){
                if(details.size == 2){
                    val ch = details[1][0]
                    if(ch == 'W' || ch == 'E' || ch == 'Y' || ch == 'M' || ch == 'D'){
                        repeat = details[1]
                    } else{
                        time = details[1]
                    }
                }
                else if(details.size == 3){
                    time = details[1]
                    repeat = details[2]
                }
            }

            if(time != ""){
                setTime.text = time
                cancelTimeBtn.visibility = View.VISIBLE
            }
            if(repeat != ""){
                setRepeat.text = repeat
                cancelRepeatBtn.visibility = View.VISIBLE
            }

            val days: Int
            val month: Int
            val year : Int

            if (date[10] == ','){
                days =  date.substring(9,10).toInt()
                year = date.substring(12,16).toInt()
            }
            else {
                days = date.substring(9, 11).toInt()
                year = date.substring(13, 17).toInt()
            }

            val months = date.substring(5,8)
            month = getMonthInt(months)

            datePicker.updateDate(year, month, days)
        }

        clickTime.setOnClickListener {
            openTimeDialog()
        }
        showTime.setOnClickListener {
            openTimeDialog()
        }

        cancelTimeBtn.setOnClickListener {
            setTime.setText(R.string.timee)
            cancelTimeBtn.visibility = View.GONE
            time = ""
        }

        clickRepeat.setOnClickListener {
            openRepeatDialog()
        }

        showRepeat.setOnClickListener {
            openRepeatDialog()
        }

        cancelRepeatBtn.setOnClickListener {
            setRepeat.setText(R.string.repeat)
            cancelRepeatBtn.visibility = View.GONE
            repeat = ""
        }

        var months: Int
        var dayOfMonth: Int
        var year: Int

        doneRemindMe.setOnClickListener {
            insideRemindMe.visibility = View.VISIBLE
            months = datePicker.month
            dayOfMonth = datePicker.dayOfMonth
            year = datePicker.year

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, months)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val days : String = when(calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "SUN"
                Calendar.MONDAY -> "MON"
                Calendar.TUESDAY -> "TUE"
                Calendar.WEDNESDAY -> "WED"
                Calendar.THURSDAY -> "THU"
                Calendar.FRIDAY -> "FRI"
                Calendar.SATURDAY -> "SAT"
                else -> ""
            }
            val month = getMonth(months)

            time = setTime.text.toString()
            repeat = setRepeat.text.toString()

            var remindMeDetail = "$days, $month $dayOfMonth, $year"
            date = remindMeDetail
            if (time == "Time" && repeat == "Repeat"){
                showRemindMeDetail.text = remindMeDetail
            }
            else if (time != "Time" && repeat == "Repeat"){
                remindMeDetail += "\n$time"
                showRemindMeDetail.text = remindMeDetail
            }
            else if (time == "Time"){
                remindMeDetail += "\n$repeat"
                showRemindMeDetail.text = remindMeDetail
            }
            else{
                remindMeDetail += "\n$time\n$repeat"
                showRemindMeDetail.text = remindMeDetail
            }
            dialogRemindMe.dismiss()
        }

        cancelRemindMe.setOnClickListener {
            dialogRemindMe.dismiss()
        }

        dialogRemindMeBelow(dialogRemindMe)
    }

    private fun getMonth(month: Int): String {
        return when (month){
            0 -> "Jan"
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "May"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Aug"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            11 -> "Dec"
            else -> ""
        }
    }

    private fun getMonthInt(months: String): Int {
        return when (months){
            "Jan" -> 0
            "Feb" -> 1
            "Mar" -> 2
            "Apr" -> 3
            "May" -> 4
            "Jun" -> 5
            "Jul" -> 6
            "Aug" -> 7
            "Sep" -> 8
            "Oct" -> 9
            "Nov" -> 10
            "Dec" -> 11
            else -> 0
        }
    }

    // Time
    @RequiresApi(Build.VERSION_CODES.M)
    private fun openTimeDialog(){
        dialogTime = dialogTime(requireContext)
        val cancelBtn = dialogTime.findViewById<TextView>(R.id.cancelBtn)
        val doneBtn = dialogTime.findViewById<TextView>(R.id.doneBtn)
        val timePicker = dialogTime.findViewById<TimePicker>(R.id.timePicker)
        val is12HoursFormat = !timePicker.is24HourView
        var hours: Int
        var minutes: Int
        var amPM: String

        if(setTime.text != "Time"){
            val timeAt = setTime.text.toString()
            hours = if( timeAt[1] == ':'){
                timeAt.subSequence(0,1).toString().toInt()  
            }
            else{
                timeAt.subSequence(0,2).toString().toInt()
            }

            minutes = if( timeAt[1] == ':'){
                timeAt.subSequence(2,4).toString().toInt()
            }
            else{
                timeAt.subSequence(3,5).toString().toInt()
            }

            amPM = if( timeAt[1] == ':'){
                timeAt.subSequence(5,7).toString()
            }
            else{
                timeAt.subSequence(6,8).toString()
            }

            if(amPM == "PM"){
                hours += 12
            }

            timePicker.hour = hours
            timePicker.minute = minutes
        }

        doneBtn.setOnClickListener {
            hours = timePicker.hour
            minutes = timePicker.minute

            amPM = if (is12HoursFormat){
                if (hours < 12){
                    "AM"
                }
                else {
                    "PM"
                }
            }
            else{
                ""
            }.toString()

            if(hours >= 12){
                hours -= 12
            }

            val minute = if(minutes < 10){
                "0$minutes"
            }
            else{
                "$minutes"
            }.toString()

            cancelTimeBtn.visibility = View.VISIBLE
            setTime.text = requireContext.getString(R.string.setTime, hours, minute, amPM)
            dialogTime.dismiss()
        }

        cancelBtn.setOnClickListener {
            if(setTime.text == "Time"){
                cancelTimeBtn.visibility = View.GONE
            }
            else{
                cancelTimeBtn.visibility = View.VISIBLE
            }
            dialogTime.dismiss()
        }

        dialogTimeBelow(dialogTime)
    }

    // Repeat
    private fun openRepeatDialog(){
        dialogRepeat = dialogRepeat(requireContext)
        selectTimeInterval = "weeks"
        repeat(dialogRepeat)
        dialogRepeatBelow(dialogRepeat)
    }

    private fun repeat(dialogRepeat: Dialog) {
        val selectBtn = dialogRepeat.findViewById<ConstraintLayout>(R.id.select)
        val days = dialogRepeat.findViewById<ConstraintLayout>(R.id.days)
        cancelRepeat = dialogRepeat.findViewById(R.id.cancelRepeat)
        doneRepeatBtn = dialogRepeat.findViewById(R.id.done)

        initializedTimeInterValAndNumber()
        initializedDaysButton()

        if(setRepeat.text != "Repeat"){
            val txtRepeat = repeat
            val txt = txtRepeat.split(" ")
            when (txt[0].trim()) {
                "Weekly" -> showRepeatWeeksDialog(1, txt)
                "Yearly" -> showRepeatDialog(1, "year")
                "Monthly" -> showRepeatDialog(1, "month")
                "Daily" -> showRepeatDialog(1, "day")
            }
            if (txt.size >= 3) when (txt[2].trim()) {
                "years" -> showRepeatDialog(txt[1].trim().toInt(), "year")
                "months" -> showRepeatDialog(txt[1].trim().toInt(), "month")
                "days" -> showRepeatDialog(txt[1].trim().toInt(), "day")
                "week" -> showRepeatWeeksDialog(txt[1].trim().toInt(), txt)
            }
        }

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

        doneRepeatBtn.setOnClickListener {
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
                        setRepeat.text = repeat
                        cancelRepeatBtn.visibility = View.VISIBLE
                        dialogRepeat.dismiss()
                    } else {
                        Toast.makeText(requireContext, "Select minimum one Days", Toast.LENGTH_SHORT).show()
                    }
                } else if (selectTimeInterval == "days") {
                    repeat = if (number.toInt() == 1) "Daily"
                    else "Every $number days"
                    setRepeat.text = repeat
                    cancelRepeatBtn.visibility = View.VISIBLE
                    dialogRepeat.dismiss()
                } else if (selectTimeInterval == "months") {
                    repeat = if (number.toInt() == 1) "Monthly"
                    else "Every $number months"
                    setRepeat.text = repeat
                    cancelRepeatBtn.visibility = View.VISIBLE
                    dialogRepeat.dismiss()
                } else if (selectTimeInterval == "years") {
                    repeat = if (number.toInt() == 1) "Yearly"
                    else "Every $number years"
                    setRepeat.text = repeat
                    cancelRepeatBtn.visibility = View.VISIBLE
                    dialogRepeat.dismiss()
                }
            } else {
                Toast.makeText(requireContext, "Enter the number", Toast.LENGTH_SHORT).show()
            }
        }

        cancelRepeat.setOnClickListener {
            initializedVariable()
            if(setRepeat.text == "Repeat")  cancelRepeatBtn.visibility = View.GONE
            else cancelRepeatBtn.visibility = View.VISIBLE
            dialogRepeat.dismiss()
        }
    }

    private fun initializedTimeInterValAndNumber() {
        addNumber = dialogRepeat.findViewById(R.id.number)
        selectMonthBtn = dialogRepeat.findViewById(R.id.selectTimeIntervals)
    }

    private fun initializedDaysButton(){
        sundayBtn = dialogRepeat.findViewById(R.id.sunday)
        mondayBtn = dialogRepeat.findViewById(R.id.monday)
        tuesdayBtn = dialogRepeat.findViewById(R.id.tuesday)
        wednesdayBtn = dialogRepeat.findViewById(R.id.wednesday)
        thursdayBtn = dialogRepeat.findViewById(R.id.thursday)
        fridayBtn = dialogRepeat.findViewById(R.id.friday)
        saturdayBtn = dialogRepeat.findViewById(R.id.saturday)
    }

    private fun initializedVariable(){
        sunday = false
        monday = false
        tuesday = false
        wednesday = false
        thursday = false
        friday = false
        saturday = false

        flagSunday = true
        flagMonday = true
        flagTuesday = true
        flagWednesday = true
        flagThursday = true
        flagFriday = true
        flagSaturday = true
    }

    private fun showRepeatDialog(number: Int,timeInterval: String){
        dialogRepeat.findViewById<ConstraintLayout>(R.id.days).visibility = View.GONE
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
    }

    private fun showRepeatWeeksDialog(number: Int, days: List<String>) {
        dialogRepeat.findViewById<ConstraintLayout>(R.id.days).visibility = View.VISIBLE
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