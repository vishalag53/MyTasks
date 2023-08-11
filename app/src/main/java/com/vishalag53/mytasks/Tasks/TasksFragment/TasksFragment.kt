package com.vishalag53.mytasks.Tasks.TasksFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.vishalag53.mytasks.Tasks.Util.menuDelete
import com.vishalag53.mytasks.Tasks.Util.menuDoubleDelete
import com.vishalag53.mytasks.Tasks.Util.setExpandBtnFunction
import com.vishalag53.mytasks.databinding.FragmentTasksBinding
import com.vishalag53.mytasks.login.LogInActivity

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var newListName: String
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        firebaseAuth = FirebaseAuth.getInstance()

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

        binding.signOut1.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext() , LogInActivity::class.java))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clAddNewList.setOnClickListener {
            val createNewListDialogBox = CreateNewListDialogBox()
            createNewListDialogBox.show(childFragmentManager,"CustomDialog")
        }
    }

    fun onNameEntered(listName: String){
        newListName = listName
        Toast.makeText(requireContext(),"$newListName",Toast.LENGTH_SHORT).show()
    }


}