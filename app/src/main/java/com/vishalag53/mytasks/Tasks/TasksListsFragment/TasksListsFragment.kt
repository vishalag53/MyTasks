package com.vishalag53.mytasks.Tasks.TasksListsFragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.Tasks.Util.TasksListCreateButtonAction
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
        setActionBarTitle(tasksListName)

        firebaseAuth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child((firebaseAuth.currentUser?.uid.toString())).child(tasksListId).child("Tasks Lists")

        tasksListsRepository = TasksListRepository(requireContext())
        tasksListsViewModel = ViewModelProvider(this,TasksListsViewModelFactory(tasksListsRepository))[TasksListsViewModel::class.java]
        tasksListCreateButtonAction = TasksListCreateButtonAction(requireContext(),databaseReference)

        binding.createBtn.setOnClickListener{ tasksListCreateButtonAction.createTask() }

    }

    private fun setActionBarTitle(listNameName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = listNameName
    }

}