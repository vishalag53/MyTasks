package com.vishalag53.mytasks.Tasks.TasksListsFragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.Tasks.Adapters.TasksListsUnCompletedTasksAdapter
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.Tasks.Util.TasksListCreateButtonAction
import com.vishalag53.mytasks.Tasks.Util.TasksListsItemUnCompleteTasksTouchHelper
import com.vishalag53.mytasks.Tasks.data.TasksList
import com.vishalag53.mytasks.databinding.FragmentTasksListsBinding


class TasksListsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsBinding
    private lateinit var tasksListsRepository: TasksListRepository
    private lateinit var tasksListsViewModel: TasksListsViewModel
    private lateinit var tasksListCreateButtonAction: TasksListCreateButtonAction
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var tasksListName: String
    private lateinit var tasksListId: String
    private lateinit var navController: NavController
    private lateinit var tasksListsUnCompletedTasksAdapter: TasksListsUnCompletedTasksAdapter
    private lateinit var mutableTasksListUnCompleteTasks: List<TasksList>
    private lateinit var mutableTasksListCompletedTasks: List<TasksList>
    private lateinit var itemUnCompleteTasksTouchHelper: ItemTouchHelper
    private lateinit var itemCompletedTasksTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListsBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        val arguments = TasksListsFragmentArgs.fromBundle(requireArguments()).tasks
        tasksListName = arguments.listNameName
        tasksListId = arguments.listNameId

        navController = Navigation.findNavController(view)

        setActionBarTitle(tasksListName)

        firebaseAuth = FirebaseAuth.getInstance()

        mutableTasksListUnCompleteTasks = mutableListOf()
        mutableTasksListCompletedTasks = mutableListOf()

        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child((firebaseAuth.currentUser?.uid.toString())).child(tasksListId).child("Tasks Lists")

        tasksListsRepository = TasksListRepository(requireContext(),databaseReference)
        tasksListsViewModel = ViewModelProvider(this,TasksListsViewModelFactory(tasksListsRepository))[TasksListsViewModel::class.java]
        tasksListCreateButtonAction = TasksListCreateButtonAction(requireContext(),databaseReference)

        binding.createBtn.setOnClickListener{ tasksListCreateButtonAction.createTask() }

        tasksListsUnCompletedTasksAdapter = TasksListsUnCompletedTasksAdapter(requireContext(),::tasksListClickListener,::importantClickListener)
        binding.rvUnCompleteTasks.adapter = tasksListsUnCompletedTasksAdapter

        tasksListsViewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableTasksListUnCompleteTasks = it
                mutableTasksListUnCompleteTasks = mutableTasksListUnCompleteTasks.reversed()
                tasksListsUnCompletedTasksAdapter.submitList(mutableTasksListUnCompleteTasks)
            }
        })

        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24)!!
        val tasksListsItemUnCompleteTasksTouchHelper = TasksListsItemUnCompleteTasksTouchHelper(requireContext(),tasksListsUnCompletedTasksAdapter,tasksListsViewModel,deleteIcon,viewLifecycleOwner,tasksListsRepository,requireActivity())

        itemUnCompleteTasksTouchHelper = ItemTouchHelper(tasksListsItemUnCompleteTasksTouchHelper)
        itemUnCompleteTasksTouchHelper.attachToRecyclerView(binding.rvUnCompleteTasks)
    }

    private fun setActionBarTitle(listNameName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = listNameName
    }

    private fun tasksListClickListener(tasksList: TasksList){
        navController.navigate(TasksListsFragmentDirections.actionTasksListsFragmentToTasksListDetailsFragment(tasksList))
    }

    private fun importantClickListener(tasksList: TasksList){
        tasksListsRepository.renameImportant(tasksList)
    }
}