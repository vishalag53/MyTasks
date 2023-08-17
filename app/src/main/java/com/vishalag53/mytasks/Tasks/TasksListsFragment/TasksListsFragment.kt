package com.vishalag53.mytasks.Tasks.TasksListsFragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.databinding.FragmentTasksListsBinding


class TasksListsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsBinding
    private lateinit var tasksListsRepository: TasksListRepository
    private lateinit var tasksListsViewModel: TasksListsViewModel

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
        setActionBarTitle(arguments.listNameName)

        tasksListsRepository = TasksListRepository(requireContext())
        tasksListsViewModel = ViewModelProvider(this,TasksListsViewModelFactory(tasksListsRepository))[TasksListsViewModel::class.java]

        binding.createBtn.setOnClickListener{ tasksListsRepository.createTask() }


    }

    private fun setActionBarTitle(listNameName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = listNameName
    }


}