package com.vishalag53.mytasks.Tasks.TasksListsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.vishalag53.mytasks.databinding.FragmentTasksListsBinding


class TasksListsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTasksListsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.lifecycleOwner = viewLifecycleOwner
        val arguments = TasksListsFragmentArgs.fromBundle(requireArguments()).tasks
        setActionBarTitle(arguments.listNameName)
    }

    private fun setActionBarTitle(listNameName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = listNameName
    }


}