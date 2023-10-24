package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.Repository.TasksListDetailsRepository
import com.vishalag53.mytasks.Tasks.TasksFragment.TasksFragmentDirections
import com.vishalag53.mytasks.Tasks.Util.MoveTasksList
import com.vishalag53.mytasks.Tasks.Util.dialogRepeat
import com.vishalag53.mytasks.Tasks.Util.dialogRepeatBelow
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.Tasks.data.TasksList
import com.vishalag53.mytasks.databinding.FragmentTasksListsDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION", "PackageName")
class TasksListsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsDetailsBinding
    private lateinit var tasksListDetailsRepository: TasksListDetailsRepository
    private lateinit var tasksListsDetailsViewModel: TasksListsDetailsViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferencePrevious: DatabaseReference
    private lateinit var databaseReferenceStarting: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var moveTasksList: MoveTasksList
    private lateinit var argumentsTaskList : TasksList
    private lateinit var argumentsNameList : NameList
    private lateinit var argumentsTasksListName : MutableList<NameList>
    private lateinit var navController: NavController
    private lateinit var taskId: String
    private lateinit var isCompleted : String
    private lateinit var title : String
    private lateinit var details : String
    private lateinit var isImportant : String
    private lateinit var date : String
    private lateinit var time : String
    private lateinit var repeat : String

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListsDetailsBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        argumentsTaskList = TasksListsDetailsFragmentArgs.fromBundle(requireArguments()).tasksList
        argumentsNameList = TasksListsDetailsFragmentArgs.fromBundle(requireArguments()).nameList
        argumentsTasksListName = TasksListsDetailsFragmentArgs.fromBundle(requireArguments()).tasksListsName.toMutableList()
        taskId = argumentsTaskList.id

        navController = Navigation.findNavController(view)

        firebaseAuth = FirebaseAuth.getInstance()

        databaseReferenceStarting = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child(firebaseAuth.currentUser?.uid.toString())

        databaseReferencePrevious = databaseReferenceStarting
            .child(argumentsNameList.listNameId)
            .child("Tasks Lists")

        databaseReference = databaseReferencePrevious.child(taskId)

        tasksListDetailsRepository = TasksListDetailsRepository(requireContext(),databaseReferencePrevious,databaseReference)
        tasksListsDetailsViewModel = ViewModelProvider(this,TasksListsDetailsViewModelFactory(tasksListDetailsRepository))[TasksListsDetailsViewModel::class.java]

        moveTasksList = MoveTasksList(argumentsTasksListName,requireContext(),argumentsNameList.listNameName,databaseReferenceStarting,argumentsTaskList,argumentsNameList,navController,argumentsTasksListName)
        binding.moveTo.text = argumentsNameList.listNameName
        if(argumentsTasksListName.size > 1){
            binding.moveTo.setOnClickListener { moveTasksList.moveTasksList() }
            binding.clMoveTO.setOnClickListener { moveTasksList.moveTasksList() }
            binding.ivMoveTO.setOnClickListener { moveTasksList.moveTasksList() }
            binding.ivMoveTO.setImageResource(R.drawable.baseline_arrow_drop_down_32_blue)
            binding.moveTo.setTextColor(ContextCompat.getColor(requireContext(),R.color.bkg_blue))
        }
        else{
            binding.moveTo.setTextColor(ContextCompat.getColor(requireContext(),R.color.md_theme_light_outline))
            binding.ivMoveTO.setImageResource(R.drawable.baseline_arrow_drop_down_32_)
        }


        // Completed Button

        tasksListsDetailsViewModel.completed.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    argumentsTaskList.isCompleted = it[0]
                    isCompleted = it[0]
                }
            }
        })

        if(argumentsTaskList.isCompleted == "true"){
            binding.checkCompleteButton.setButtonDrawable(R.drawable.check_circle_32px)
            val title = argumentsTaskList.title
            val spannableTitle = SpannableString(title)
            val strikethroughSpan = StrikethroughSpan()
            spannableTitle.setSpan(strikethroughSpan,0,title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.title.text = Editable.Factory.getInstance().newEditable(spannableTitle)
            binding.title.setTextColor(requireContext().getColor(R.color.md_theme_light_outline))
        }
        else{
            binding.checkCompleteButton.setButtonDrawable(R.drawable.radio_button_unchecked_32px)
            binding.title.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.title)
            val configuration = requireContext().resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                binding.title.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                binding.title.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            }
        }

        binding.checkCompleteButton.setOnClickListener{
            if(isCompleted == "true"){
                binding.checkCompleteButton.setButtonDrawable(R.drawable.radio_button_unchecked_32px)
                databaseReference.child("Completed").setValue("false")
                binding.title.text = Editable.Factory.getInstance().newEditable(title)
                val configuration = requireContext().resources.configuration
                if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                    binding.title.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                }
                else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                    binding.title.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                }
            }
            else{
                binding.checkCompleteButton.setButtonDrawable(R.drawable.check_circle_32px)
                val spannableTitle = SpannableString(title)
                val strikethroughSpan = StrikethroughSpan()
                spannableTitle.setSpan(strikethroughSpan,0,title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.title.text = Editable.Factory.getInstance().newEditable(spannableTitle)
                binding.title.setTextColor(requireContext().getColor(R.color.md_theme_light_outline))
                databaseReference.child("Completed").setValue("true")
            }
        }

        // title

        binding.title.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newTitle = s.toString()
                tasksListsDetailsViewModel.setNewTitle(newTitle)
            }
        })

        tasksListsDetailsViewModel.newTitle.observe(viewLifecycleOwner,Observer{
            it?.let {
                databaseReference.child("Title").setValue(it)
                argumentsTaskList.title = it
            }
        })

        tasksListsDetailsViewModel.title.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    title = it[0]
                    argumentsTaskList.title = title
                    setActionBarTitle(title)
                }
            }
        })

        // important

        tasksListsDetailsViewModel.important.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    isImportant = it[0]
                    argumentsTaskList.important = isImportant
                }
            }
        })

        if(argumentsTaskList.important == "true"){
            binding.addImportantss.setBackgroundResource(R.drawable.baseline_star_24)
        }
        else{
            binding.addImportantss.setBackgroundResource(R.drawable.baseline_star_outline_24)
        }

        binding.addImportantss.setOnClickListener {
            if(isImportant == "true"){
                databaseReference.child("Important").setValue("false")
                binding.addImportantss.setBackgroundResource(R.drawable.baseline_star_outline_24)
            }
            else{
                databaseReference.child("Important").setValue("true")
                binding.addImportantss.setBackgroundResource(R.drawable.baseline_star_24)
            }
        }

        // detail

        tasksListsDetailsViewModel.details.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    details = it[0]
                    argumentsTaskList.details = details
                }
            }
        })

        binding.addDetail.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.details)
        if(argumentsTaskList.details!!.isEmpty()) {
            binding.cancelDetailBtns.visibility = View.GONE
        }
        else{
            binding.cancelDetailBtns.visibility = View.VISIBLE
        }
        binding.addDetail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val details = s.toString()
                tasksListsDetailsViewModel.setNewDetails(details)
            }
        })

        tasksListsDetailsViewModel.newDetails.observe(viewLifecycleOwner, Observer {
            it?.let {
                details = it
                databaseReference.child("Details").setValue(details)
                argumentsTaskList.details = details
                if(details.isEmpty()){
                    binding.cancelDetailBtns.visibility = View.GONE
                }
                else{
                    binding.cancelDetailBtns.visibility = View.VISIBLE
                }
            }
        })

        binding.cancelDetailBtns.setOnClickListener {
            details = ""
            argumentsTaskList.details = details
            tasksListsDetailsViewModel.setNewDetails(details)
            binding.cancelDetailBtns.visibility = View.GONE
            binding.addDetail.text = Editable.Factory.getInstance().newEditable(details)
        }

        // date

        tasksListsDetailsViewModel.date.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    date = it[0]
                    argumentsTaskList.date = date
                }
            }
        })

        binding.clCalendar.setOnClickListener { dateAction() }
        binding.calendarBtn.setOnClickListener { dateAction() }

        tasksListsDetailsViewModel.newDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                date = it
                databaseReference.child("Date").setValue(date)
                argumentsTaskList.date = date
            }
        })

        // time

        tasksListsDetailsViewModel.time.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    time = it[0]
                    argumentsTaskList.time = time
                }
            }
        })

        binding.clTime.setOnClickListener { timeAction() }
        binding.TimeBtn.setOnClickListener { timeAction() }

        tasksListsDetailsViewModel.newTime.observe(viewLifecycleOwner, Observer {
            it?.let {
                time = it
                databaseReference.child("Time").setValue(time)
                argumentsTaskList.time = time
            }
        })

        // repeat

        tasksListsDetailsViewModel.repeat.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                it?.let {
                    repeat = it[0]
                    argumentsTaskList.repeat = repeat
                    if(repeat.isEmpty()){
                        initializedVariable()
                    }
                }
            }
        })

        if(argumentsTaskList.repeat!!.isNotEmpty()){
            binding.showRepeatDetail.visibility = View.VISIBLE
            binding.showRepeatDetail.text = argumentsTaskList.repeat
        }
        else{
           // binding.cancelRepeatBtn.visibility = View.GONE
        }

//        binding.cancelRepeatBtn.setOnClickListener {
//            repeat = ""
//            binding.cancelRepeatBtn.visibility = View.GONE
//            binding.showRepeatDetail.text = repeat
//            tasksListsDetailsViewModel.setNewRepeat(repeat)
//            initializedVariable()
//        }

        binding.clRepeat.setOnClickListener { repeatAction() }
        binding.RepeatBtn.setOnClickListener { repeatAction() }

        tasksListsDetailsViewModel.newRepeat.observe(viewLifecycleOwner, Observer {
            it?.let {
                repeat = it
                databaseReference.child("Repeat").setValue(repeat)
                argumentsTaskList.repeat = repeat
                if(repeat.isNotEmpty()){
                    binding.showRepeatDetail.visibility = View.VISIBLE
                    binding.showRepeatDetail.text = repeat
//                    binding.cancelRepeatBtn.visibility = View.VISIBLE
                }
                else{
//                    binding.cancelRepeatBtn.visibility = View.GONE
                }
            }
        })

    }

    private fun setActionBarTitle(taskName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = taskName
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_overflow_tasks_list_details,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.delete -> {
                databaseReference.removeValue()
                navController.navigate(TasksListsDetailsFragmentDirections.actionTasksListDetailsFragmentToTasksFragment2())
                navController.navigate(TasksFragmentDirections.actionTasksFragmentToTasksListsFragment(argumentsNameList,argumentsTasksListName.toTypedArray()))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Calendar

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dateAction() {
        if (date.isNotEmpty()) setDateOnCalendar()
        else showDatePicker()
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerAtSpecificDate(days: Int, month: Int, years: Int) {
        val datePicker = DatePickerDialog(requireContext(),{ _, year,monthOfYear, dayOfMonth ->
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
        tasksListsDetailsViewModel.setNewDate(date)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val currentDateTime = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(),{ _, year, monthOfYear,dayOfMonth ->
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

    // time

    private fun timeAction(){
        if(time.isNotEmpty()) setTimeOnCalendar()
        else showTimePicker()
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
        val timePicker = TimePickerDialog(requireContext(),{ _, selectedHour,selectedMinute ->
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
        tasksListsDetailsViewModel.setNewTime(time)
    }

    private fun showTimePicker() {
        val selectedDate = Calendar.getInstance()
        val timePicker = TimePickerDialog(requireContext(),{ _, hourOfDay, minute ->
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

    // repeat

    private fun repeatAction(){
        if(repeat.isNotEmpty()) setRepeat()
        else showRepeatDialog()
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

    private fun showRepeatWeeksDialog(number: Int, days: List<String>) {
        dialog = dialogRepeat(requireContext())
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

    private fun showRepeatDialog(number: Int,timeInterval: String){
        dialog = dialogRepeat(requireContext())
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

    private fun showRepeatDialog() {
        dialog = dialogRepeat(requireContext())
        selectTimeInterval = "weeks"
        repeat(dialog)
        dialogRepeatBelow(dialog)
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
        val configuration = requireContext().resources.configuration
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
        val configuration = requireContext().resources.configuration
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
        val configuration = requireContext().resources.configuration
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
        val configuration = requireContext().resources.configuration
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
        val configuration = requireContext().resources.configuration
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
        val configuration = requireContext().resources.configuration
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
        val configuration = requireContext().resources.configuration
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

    private fun repeat(dialog: Dialog) {
        val selectBtn = dialog.findViewById<ConstraintLayout>(R.id.select)
        val days = dialog.findViewById<ConstraintLayout>(R.id.days)
        cancelBtn = dialog.findViewById(R.id.cancelRemindMe)
        doneBtn = dialog.findViewById(R.id.done)

        initializedTimeInterValAndNumber()
        initializedDaysButton()

        selectBtn.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            val inflater: MenuInflater = popupMenu.menuInflater

            inflater.inflate(R.menu.menu_time_intervals, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.daysMenu -> {
                        selectTimeInterval = requireContext().getString(R.string.days)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.GONE
                        true
                    }
                    R.id.weeksMenu -> {
                        selectTimeInterval = requireContext().getString(R.string.weeks)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.VISIBLE
                        true
                    }
                    R.id.monthsMenu -> {
                        selectTimeInterval = requireContext().getString(R.string.months)
                        selectMonthBtn.text = selectTimeInterval
                        days.visibility = View.GONE
                        true
                    }
                    R.id.yearsMenu -> {
                        selectTimeInterval = requireContext().getString(R.string.years)
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
            val configuration = requireContext().resources.configuration
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
            val configuration = requireContext().resources.configuration
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
            val configuration = requireContext().resources.configuration
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
            val configuration = requireContext().resources.configuration
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
            val configuration = requireContext().resources.configuration
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
            val configuration = requireContext().resources.configuration
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
            val configuration = requireContext().resources.configuration
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
                        Toast.makeText(requireContext(), "Select minimum one Days", Toast.LENGTH_SHORT).show()
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

//                binding.cancelRepeatBtn.visibility = View.VISIBLE
                binding.showRepeatDetail.visibility = View.VISIBLE
                binding.showRepeatDetail.text = repeat
                tasksListsDetailsViewModel.setNewRepeat(repeat)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Enter the number", Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }
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
}