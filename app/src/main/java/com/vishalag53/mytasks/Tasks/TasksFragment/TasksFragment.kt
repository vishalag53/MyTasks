package com.vishalag53.mytasks.Tasks.TasksFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.vishalag53.mytasks.Tasks.Adapters.TasksAdapter
import com.vishalag53.mytasks.Tasks.Repository.TasksRepository
import com.vishalag53.mytasks.Tasks.Util.TasksItemTouchHelper
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.FragmentTasksBinding
import java.util.Locale

@Suppress("DEPRECATION")
class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var mutableNameList: List<NameList>
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var tasksRepository: TasksRepository
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater)
        setActionBarTitle()
        setHasOptionsMenu(true)
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

        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child((firebaseAuth.currentUser?.uid.toString()))

        tasksRepository = TasksRepository(databaseReference,requireContext())
        tasksViewModel = ViewModelProvider(this,TasksViewModelFactory(tasksRepository))[TasksViewModel::class.java]

        tasksViewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableNameList = it
                mutableNameList = mutableNameList.reversed()
                tasksAdapter.submitList(mutableNameList)
            }
        })

        tasksAdapter = TasksAdapter(::taskClickListener)
        binding.rvTasks.adapter = tasksAdapter

        val deleteIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_delete_24)!!
        val tasksItemTouchHelper = TasksItemTouchHelper(requireContext(),tasksAdapter,tasksViewModel,deleteIcon,viewLifecycleOwner,tasksRepository,requireActivity())

        itemTouchHelper = ItemTouchHelper(tasksItemTouchHelper)

        itemTouchHelper.attachToRecyclerView(binding.rvTasks)

        tasksViewModel.sortType.observe(viewLifecycleOwner, Observer { type ->
            when (type) {
                "Name ASC" -> {
                    val tmpMutableNameList = mutableNameList.sortedBy {list -> list.listNameName }
                    tasksAdapter.submitList(tmpMutableNameList)
                }
                "Name DESC" -> {
                    val tmpMutableNameList = mutableNameList.sortedByDescending {list -> list.listNameName }
                    tasksAdapter.submitList(tmpMutableNameList)
                }
                "Default" -> {
                    tasksAdapter.submitList(mutableNameList)
                }
            }
        })

        binding.createBtn.setOnClickListener { tasksRepository.createTask() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            navController.popBackStack(R.id.logInActivity,false)
            goToHomeScreenOfMobile()
        }

        tasksViewModel.getNameList().observe(viewLifecycleOwner, Observer {
            it?.let{
                tasksAdapter.submitList(it.reversed())
            }
        })

    }

    private fun taskClickListener(nameList: NameList){
        navController.navigate(TasksFragmentDirections.actionTasksFragmentToTasksListsFragment(nameList))
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

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_overflow_tasks,menu)

        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.reorderTasks -> {
                TODO()
                true
            }
            R.id.search -> {
                true
            }
            R.id.defaultSort -> {
                tasksViewModel.getSortType("Default")
                true
            }
            R.id.nameAsc -> {
                tasksViewModel.getSortType("Name ASC")
                true
            }
            R.id.nameDesc -> {
                tasksViewModel.getSortType("Name DESC")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

object CacheManager {
    private val cacheMap: MutableMap<String, List<NameList>?> = mutableMapOf()

    fun put(key: String, value: List<NameList>?) {
        cacheMap[key] = value
    }

    fun get(key: String): List<NameList>? {
        return cacheMap[key]
    }

    fun containsKey(key: String): Boolean {
        return cacheMap.containsKey(key)
    }

    fun clearCache() {
        cacheMap.clear()
    }
}