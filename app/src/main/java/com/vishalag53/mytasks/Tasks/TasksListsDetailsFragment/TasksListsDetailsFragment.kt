package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.Repository.TasksListDetailsRepository
import com.vishalag53.mytasks.Tasks.data.TasksList
import com.vishalag53.mytasks.databinding.FragmentTasksListsDetailsBinding

class TasksListsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsDetailsBinding
    private lateinit var tasksListDetailsRepository: TasksListDetailsRepository
    private lateinit var tasksListsDetailsViewModel: TasksListsDetailsViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferencePrevious: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var taskList: TasksList
    private lateinit var taskName: String
    private lateinit var taskId: String

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
        taskName = argumentsTaskList.title
        taskId = argumentsTaskList.id
        setActionBarTitle(taskName)

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
        }

        if(argumentsTaskList.important == "true"){
            binding.addImportant.setBackgroundResource(R.drawable.baseline_star_24)
        }
        else{
            binding.addImportant.setBackgroundResource(R.drawable.baseline_star_outline_24)
        }

        binding.addDetails.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.details)
        binding.calendar.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.date)
        binding.time.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.time)
        binding.repeat.text = Editable.Factory.getInstance().newEditable(argumentsTaskList.repeat)

    }

    private fun setActionBarTitle(taskName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = taskName
    }

}