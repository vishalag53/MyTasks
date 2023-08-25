package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.vishalag53.mytasks.databinding.FragmentTasksListsDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TasksListsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsDetailsBinding
    private lateinit var tasksListDetailsRepository: TasksListDetailsRepository
    private lateinit var tasksListsDetailsViewModel: TasksListsDetailsViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferencePrevious: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var taskId: String
    private lateinit var isCompleted : String
    private lateinit var title : String
    private lateinit var details : String
    private lateinit var isImportant : String
    private lateinit var date : String
    private lateinit var time : String
    private lateinit var repeat : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListsDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        val argumentsTaskList = TasksListsDetailsFragmentArgs.fromBundle(requireArguments()).tasksList
        val argumentsNameList = TasksListsDetailsFragmentArgs.fromBundle(requireArguments()).nameList
        taskId = argumentsTaskList.id

        navController = Navigation.findNavController(view)

        firebaseAuth = FirebaseAuth.getInstance()

        databaseReferencePrevious = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child(firebaseAuth.currentUser?.uid.toString())
            .child(argumentsNameList.listNameId)
            .child("Tasks Lists")

        databaseReference = databaseReferencePrevious.child(taskId)

        tasksListDetailsRepository = TasksListDetailsRepository(requireContext(),databaseReferencePrevious,databaseReference)
        tasksListsDetailsViewModel = ViewModelProvider(this,TasksListsDetailsViewModelFactory(tasksListDetailsRepository))[TasksListsDetailsViewModel::class.java]

        // Completed Button

        tasksListsDetailsViewModel.completed.observe(viewLifecycleOwner, Observer {
            it?.let {
                argumentsTaskList.isCompleted = it[0]
                isCompleted = it[0]
            }
        })

        if(argumentsTaskList.isCompleted == "true"){
            binding.checkCompleteButton.setButtonDrawable(R.drawable.check_circle_32px)

            val title = title
            val spannableTitle = SpannableString(title)
            val strikethroughSpan = StrikethroughSpan()
            spannableTitle.setSpan(strikethroughSpan,0,title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.title.text = Editable.Factory.getInstance().newEditable(spannableTitle)
            binding.title.setTextColor(requireContext().getColor(R.color.md_theme_light_outline))
        }
        else{
            binding.checkCompleteButton.setButtonDrawable(R.drawable.radio_button_unchecked_32px)
            binding.title.setTextColor(requireContext().getColor(R.color.black))
            binding.title.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.title)
        }

        binding.checkCompleteButton.setOnClickListener{
            if(isCompleted == "true"){
                binding.checkCompleteButton.setButtonDrawable(R.drawable.radio_button_unchecked_32px)
                databaseReference.child("Completed").setValue("false")
                binding.title.setTextColor(requireContext().getColor(R.color.black))
                binding.title.text = Editable.Factory.getInstance().newEditable(title)
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
            it?.let {
                title = it[0]
                argumentsTaskList.title = title
                setActionBarTitle(title)
            }
        })

        // important

        tasksListsDetailsViewModel.important.observe(viewLifecycleOwner, Observer {
            it?.let {
                isImportant = it[0]
                argumentsTaskList.important = isImportant
            }
        })

        if(argumentsTaskList.important == "true"){
            binding.addImportant.setBackgroundResource(R.drawable.baseline_star_24)
        }
        else{
            binding.addImportant.setBackgroundResource(R.drawable.baseline_star_outline_24)
        }

        binding.addImportant.setOnClickListener {
            if(isImportant == "true"){
                databaseReference.child("Important").setValue("false")
                binding.addImportant.setBackgroundResource(R.drawable.baseline_star_outline_24)
            }
            else{
                databaseReference.child("Important").setValue("true")
                binding.addImportant.setBackgroundResource(R.drawable.baseline_star_24)
            }
        }

        // detail

        tasksListsDetailsViewModel.details.observe(viewLifecycleOwner, Observer {
            it?.let {
                details = it[0]
                argumentsTaskList.details = details
            }
        })

        binding.addDetails.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.details)
        binding.addDetails.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val details = s.toString()
                tasksListsDetailsViewModel.setNewDetails(details)
            }
        })

        tasksListsDetailsViewModel.newDetails.observe(viewLifecycleOwner, Observer {
            it?.let {
                databaseReference.child("Details").setValue(it)
                argumentsTaskList.details = details
            }
        })

        // date

        tasksListsDetailsViewModel.date.observe(viewLifecycleOwner, Observer {
            it?.let {
                date = it[0]
                argumentsTaskList.date = date
            }
        })

        if(argumentsTaskList.date!!.isNotEmpty()){
            binding.calendar.visibility = View.GONE
            binding.showDateDetail.text = argumentsTaskList.date
        }
        else{
            binding.insideDate.visibility = View.GONE
            binding.cancelDateBtn.visibility = View.GONE
        }

        binding.cancelDateBtn.setOnClickListener {
            date = ""
            binding.insideDate.visibility = View.GONE
            binding.cancelDateBtn.visibility = View.GONE
            binding.calendar.visibility = View.VISIBLE
            tasksListsDetailsViewModel.setNewDate(date)
        }

        binding.clCalendar.setOnClickListener { dateAction() }
        binding.calendarBtn.setOnClickListener { dateAction() }
        binding.calendar.setOnClickListener { dateAction() }
        binding.insideDate.setOnClickListener { setDateOnCalendar() }

        tasksListsDetailsViewModel.newDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                date = it
                argumentsTaskList.date = date
                databaseReference.child("Date").setValue(date)
                argumentsTaskList.date = date
                if(date.isNotEmpty()){
                    binding.showDateDetail.text = it
                    binding.cancelDateBtn.visibility = View.VISIBLE
                    binding.insideDate.visibility = View.VISIBLE
                    binding.calendar.visibility = View.GONE
                }
                else{
                    binding.insideDate.visibility = View.GONE
                    binding.cancelDateBtn.visibility = View.GONE
                    binding.calendar.visibility = View.VISIBLE
                }
            }
        })

        // time

        tasksListsDetailsViewModel.time.observe(viewLifecycleOwner, Observer {
            it?.let {
                time = it[0]
                argumentsTaskList.time = time
            }
        })

        if(argumentsTaskList.time!!.isNotEmpty()){
            binding.time.visibility = View.GONE
            binding.showTimeDetail.text = argumentsTaskList.time
        }
        else{
            binding.insideTime.visibility = View.GONE
            binding.cancelTimeBtn.visibility = View.GONE
        }

        binding.cancelTimeBtn.setOnClickListener {
            time = ""
            binding.insideTime.visibility = View.GONE
            binding.cancelTimeBtn.visibility = View.GONE
            binding.time.visibility = View.VISIBLE
            databaseReference.child("Time").setValue(time)
        }

        // repeat

        tasksListsDetailsViewModel.repeat.observe(viewLifecycleOwner, Observer {
            it?.let {
                repeat = it[0]
                argumentsTaskList.repeat = repeat
            }
        })

        if(argumentsTaskList.repeat!!.isNotEmpty()){
            binding.repeat.visibility = View.GONE
            binding.showRepeatDetail.text = argumentsTaskList.repeat
        }
        else{
            binding.insideRepeat.visibility = View.GONE
            binding.cancelRepeatBtn.visibility = View.GONE
        }

        binding.cancelRepeatBtn.setOnClickListener {
            repeat = ""
            binding.insideRepeat.visibility = View.GONE
            binding.cancelRepeatBtn.visibility = View.GONE
            binding.repeat.visibility = View.VISIBLE

            databaseReference.child("Repeat").setValue(repeat)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dateAction() {
        if (date.isNotEmpty()) setDateOnCalendar()
        else showDatePicker()
    }

    private fun setActionBarTitle(taskName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = taskName
    }

    // Calendar

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
        binding.insideDate.visibility = View.VISIBLE
        binding.cancelDateBtn.visibility = View.VISIBLE
        binding.showDateDetail.text = date
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


}