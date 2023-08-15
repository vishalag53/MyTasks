package com.vishalag53.mytasks.Tasks.TasksListsDetailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.FragmentTasksListsBinding
import com.vishalag53.mytasks.databinding.FragmentTasksListsDetailsBinding

class TasksListsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTasksListsDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun setActionBarTitle() {
        //(activity as AppCompatActivity).supportActionBar?.title =
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {


        setActionBarTitle()
    }

}