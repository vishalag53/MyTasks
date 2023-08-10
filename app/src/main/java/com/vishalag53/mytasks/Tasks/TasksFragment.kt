package com.vishalag53.mytasks.Tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vishalag53.mytasks.Util.menuDelete
import com.vishalag53.mytasks.Util.menuDoubleDelete
import com.vishalag53.mytasks.Util.setExpandBtnFunction
import com.vishalag53.mytasks.databinding.FragmentTasksBinding

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.menuBtn1.setOnClickListener {
            // double
            menuDoubleDelete(it,requireContext())
        }

        binding.menuBtn2.setOnClickListener {
            // double
            menuDoubleDelete(it, requireContext())
        }

        binding.menuBtn4.setOnClickListener {
            // single
            menuDelete(it,requireContext())
        }

        binding.expendBtn.setOnClickListener {
            setExpandBtnFunction(binding,resources,it,requireContext())
        }

        return binding.root
    }


}