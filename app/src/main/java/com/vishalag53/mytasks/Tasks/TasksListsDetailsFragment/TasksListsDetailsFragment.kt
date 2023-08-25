package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

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

    @RequiresApi(Build.VERSION_CODES.M)
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
            binding.title.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.title)
        }

        binding.checkCompleteButton.setOnClickListener{
            if(isCompleted == "true"){
                binding.checkCompleteButton.setButtonDrawable(R.drawable.radio_button_unchecked_32px)
                databaseReference.child("Completed").setValue("false")
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
            }
        })

        tasksListsDetailsViewModel.title.observe(viewLifecycleOwner, Observer {
            it?.let {
                title = it[0]
                setActionBarTitle(title)
            }
        })

        // important

        tasksListsDetailsViewModel.important.observe(viewLifecycleOwner, Observer {
            it?.let {
                isImportant = it[0]
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
            }
        })

        // date

        tasksListsDetailsViewModel.date.observe(viewLifecycleOwner, Observer {
            it?.let {
                date = it[0]
            }
        })

        binding.calendar.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.date)

        if(argumentsTaskList.date!!.isNotEmpty()){
            binding.calendar.visibility = View.GONE

        }
        else{
            binding.insideDate.visibility = View.GONE
            binding.cancelDateBtn.visibility = View.GONE

        }

        // time

        tasksListsDetailsViewModel.time.observe(viewLifecycleOwner, Observer {
            it?.let {
                time = it[0]
            }
        })

        binding.time.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.time)

        if(argumentsTaskList.time!!.isNotEmpty()){
            binding.time.visibility = View.GONE

        }
        else{
            binding.insideTime.visibility = View.GONE
            binding.cancelTimeBtn.visibility = View.GONE
        }

        // repeat

        tasksListsDetailsViewModel.repeat.observe(viewLifecycleOwner, Observer {
            it?.let {
                repeat = it[0]
            }
        })

        binding.repeat.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.repeat)

        if(argumentsTaskList.repeat!!.isNotEmpty()){
            binding.repeat.visibility = View.GONE

        }
        else{
            binding.insideRepeat.visibility = View.GONE
            binding.cancelRepeatBtn.visibility = View.GONE
        }

    }

    private fun setActionBarTitle(taskName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = taskName
    }

}