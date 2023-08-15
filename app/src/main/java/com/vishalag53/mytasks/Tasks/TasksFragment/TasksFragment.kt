package com.vishalag53.mytasks.Tasks.TasksFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import com.vishalag53.mytasks.Tasks.Adapters.TasksFragmentAdapter
import com.vishalag53.mytasks.Tasks.Repository.TasksRepository
import com.vishalag53.mytasks.Tasks.Util.TasksItemTouchHelper
import com.vishalag53.mytasks.Tasks.Util.setSortBtnFunction
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.FragmentTasksBinding
import java.util.Locale

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var tasksAdapter: TasksFragmentAdapter
    private lateinit var mutableNameList: List<NameList>
    private lateinit var tasksViewModel: TasksFragmentViewModel
    private lateinit var tasksRepository: TasksRepository
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater)
        setActionBarTitle()
        return binding.root
    }

    private fun setActionBarTitle() {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.myTasks)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()
        mutableNameList = mutableListOf()
        searchView = binding.searchView

        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child((firebaseAuth.currentUser?.uid.toString()))

        tasksRepository = TasksRepository(databaseReference,requireContext())
        tasksViewModel = ViewModelProvider(this,TasksFragmentViewModelFactory(tasksRepository))[TasksFragmentViewModel::class.java]

        tasksViewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableNameList = it
                tasksAdapter.addHeaderAndSubmitList(mutableNameList)
            }
        })

        tasksAdapter = TasksFragmentAdapter(::taskClickListener, ::renameClickListener)
        binding.rvTasks.adapter = tasksAdapter

        val deleteIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_delete_24)!!
        val tasksItemTouchHelper = TasksItemTouchHelper(requireContext(),tasksAdapter,tasksViewModel,deleteIcon,viewLifecycleOwner)

        itemTouchHelper = ItemTouchHelper(tasksItemTouchHelper)

        itemTouchHelper.attachToRecyclerView(binding.rvTasks)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               filterList(newText)
                return true
            }

        })

        binding.sortBtn.setOnClickListener { setSortBtnFunction(binding,resources,it,requireContext()) }

        binding.cvCreateTasks.setOnClickListener { tasksViewModel.getCreate() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            navController.popBackStack(R.id.logInActivity,false)
            goToHomeScreenOfMobile()
        }

    }

    private fun taskClickListener(nameList: NameList){
        navController.navigate(TasksFragmentDirections.actionTasksFragmentToTasksListsFragment(nameList))
    }

    private fun renameClickListener(nameList: NameList){
        tasksViewModel.getRename(nameList)
    }

    private fun filterList(query: String?){
        if(query != null){
            val filteredList = ArrayList<NameList>()
            for (i in mutableNameList){
                if (i.listNameName.lowercase(Locale.ROOT).contains(query)){
                    filteredList.add(i)
                }
            }

            if(filteredList.isEmpty()){
                Toast.makeText(requireContext(),"No Task list found",Toast.LENGTH_SHORT).show()
            }
            else {
                tasksAdapter.setFilteredList(filteredList)
            }
        }
    }

    private fun goToHomeScreenOfMobile() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}