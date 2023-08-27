package com.vishalag53.mytasks.Tasks.TasksListsFragment

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.vishalag53.mytasks.Tasks.Adapters.TasksListsCompletedTasksAdapter
import com.vishalag53.mytasks.Tasks.Adapters.TasksListsUnCompletedTasksAdapter
import com.vishalag53.mytasks.Tasks.Repository.TasksListRepository
import com.vishalag53.mytasks.Tasks.Util.TasksListCreateButtonAction
import com.vishalag53.mytasks.Tasks.Util.TasksListsItemCompletedTasksTouchHelper
import com.vishalag53.mytasks.Tasks.Util.TasksListsItemUnCompleteTasksTouchHelper
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.Tasks.data.TasksList
import com.vishalag53.mytasks.databinding.FragmentTasksListsBinding
import java.util.Locale

@Suppress("DEPRECATION")
class TasksListsFragment : Fragment() {

    private lateinit var binding: FragmentTasksListsBinding
    private lateinit var tasksListsRepository: TasksListRepository
    private lateinit var tasksListsViewModel: TasksListsViewModel
    private lateinit var tasksListCreateButtonAction: TasksListCreateButtonAction
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferencePrevious: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var tasksListName: String
    private lateinit var tasksListId: String
    private lateinit var navController: NavController
    private lateinit var tasksListsUnCompletedTasksAdapter: TasksListsUnCompletedTasksAdapter
    private lateinit var tasksListsCompletedTasksAdapter: TasksListsCompletedTasksAdapter
    private lateinit var mutableTasksListUnCompleteTasks: List<TasksList>
    private lateinit var mutableTasksListCompletedTasks: List<TasksList>
    private lateinit var itemUnCompleteTasksTouchHelper: ItemTouchHelper
    private lateinit var itemCompletedTasksTouchHelper: ItemTouchHelper
    private lateinit var arguments : NameList
    private lateinit var argumentsTasksListName : MutableList<NameList>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksListsBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        arguments = TasksListsFragmentArgs.fromBundle(requireArguments()).tasks
        argumentsTasksListName = TasksListsFragmentArgs.fromBundle(requireArguments()).tasksLists.toMutableList()
        tasksListName = arguments.listNameName
        tasksListId = arguments.listNameId
        setActionBarTitle(tasksListName)

        navController = Navigation.findNavController(view)

        firebaseAuth = FirebaseAuth.getInstance()

        mutableTasksListUnCompleteTasks = mutableListOf()
        mutableTasksListCompletedTasks = mutableListOf()

        databaseReferencePrevious = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child(firebaseAuth.currentUser?.uid.toString()).child(tasksListId)

        databaseReference = databaseReferencePrevious.child("Tasks Lists")

        tasksListsRepository = TasksListRepository(requireContext(),databaseReference,databaseReferencePrevious)
        tasksListsViewModel = ViewModelProvider(this,TasksListsViewModelFactory(tasksListsRepository))[TasksListsViewModel::class.java]
        tasksListCreateButtonAction = TasksListCreateButtonAction(requireContext(),databaseReference)

        tasksListsViewModel.tasksName.observe(viewLifecycleOwner, Observer {
            it?.let {
                setActionBarTitle(it[0])
            }
        })

        binding.createBtn.setOnClickListener{ tasksListCreateButtonAction.createTask() }

        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24)!!

        // UnComplete Tasks

        tasksListsUnCompletedTasksAdapter = TasksListsUnCompletedTasksAdapter(requireContext(),::tasksListClickListener,::importantClickListener,::completeTasksClickListener)
        binding.rvUnCompleteTasks.adapter = tasksListsUnCompletedTasksAdapter

        tasksListsViewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableTasksListUnCompleteTasks = it
                mutableTasksListUnCompleteTasks = mutableTasksListUnCompleteTasks.reversed()
                tasksListsUnCompletedTasksAdapter.submitList(mutableTasksListUnCompleteTasks)
            }
        })

        val tasksListsItemUnCompleteTasksTouchHelper = TasksListsItemUnCompleteTasksTouchHelper(
            requireContext(),
            tasksListsUnCompletedTasksAdapter,
            tasksListsViewModel,
            deleteIcon,
            viewLifecycleOwner,
            tasksListsRepository,
            requireActivity())

        itemUnCompleteTasksTouchHelper = ItemTouchHelper(tasksListsItemUnCompleteTasksTouchHelper)
        itemUnCompleteTasksTouchHelper.attachToRecyclerView(binding.rvUnCompleteTasks)

        // Completed Tasks

        tasksListsCompletedTasksAdapter = TasksListsCompletedTasksAdapter(requireContext(),::tasksListClickListener,::importantCompletedTasksClickListener,::unCompleteTasksListener)
        binding.rvCompleteTasks.adapter = tasksListsCompletedTasksAdapter

        tasksListsViewModel.dataCompletedTasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                mutableTasksListCompletedTasks = it
                if(mutableTasksListCompletedTasks.isNotEmpty()){
                    binding.clCompleteTasks.visibility = View.VISIBLE
                }
                else{
                    binding.clCompleteTasks.visibility = View.GONE
                }
                binding.tvCompleteWithNumber.text = getString(R.string.completed_tasks,mutableTasksListCompletedTasks.size)
                mutableTasksListCompletedTasks = mutableTasksListCompletedTasks.reversed()
                tasksListsCompletedTasksAdapter.submitList(mutableTasksListCompletedTasks)
            }
        })

        val tasksListsItemCompletedTasksTouchHelper = TasksListsItemCompletedTasksTouchHelper(
            requireContext(),
            tasksListsCompletedTasksAdapter,
            tasksListsViewModel,
            deleteIcon,
            viewLifecycleOwner,
            tasksListsRepository,
            requireActivity())

        itemCompletedTasksTouchHelper = ItemTouchHelper(tasksListsItemCompletedTasksTouchHelper)
        itemCompletedTasksTouchHelper.attachToRecyclerView(binding.rvCompleteTasks)

        var flag = true

        binding.clCompleteTasks.setOnClickListener {
            val configuration = requireContext().resources.configuration
            if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                flag = if (flag) {
                    binding.rvCompleteTasks.visibility = View.VISIBLE
                    binding.iconUpDown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24_night)
                    false
                } else {
                    binding.rvCompleteTasks.visibility = View.GONE
                    binding.iconUpDown.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_night)
                    true
                }
            } else if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                flag = if (flag) {
                    binding.rvCompleteTasks.visibility = View.VISIBLE
                    binding.iconUpDown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24_day)
                    false
                } else {
                    binding.rvCompleteTasks.visibility = View.GONE
                    binding.iconUpDown.setImageResource(R.drawable.baseline_keyboard_arrow_right_24_day)
                    true
                }
            }
        }

        tasksListsViewModel.sortType.observe(viewLifecycleOwner, Observer { type ->
            when (type) {
                "Name ASC" -> {
                    val tmpMutableNameList = mutableTasksListUnCompleteTasks.sortedBy {list -> list.title }
                    tasksListsUnCompletedTasksAdapter.submitList(tmpMutableNameList)

                    val tmpMutableNameList1 = mutableTasksListCompletedTasks.sortedBy { list -> list.title }
                    tasksListsCompletedTasksAdapter.submitList(tmpMutableNameList1)
                }
                "Name DESC" -> {
                    val tmpMutableNameList = mutableTasksListUnCompleteTasks.sortedByDescending {list -> list.title }
                    tasksListsUnCompletedTasksAdapter.submitList(tmpMutableNameList)

                    val tmpMutableNameList1 = mutableTasksListCompletedTasks.sortedByDescending { list -> list.title }
                    tasksListsCompletedTasksAdapter.submitList(tmpMutableNameList1)
                }
                "Default" -> {
                    tasksListsUnCompletedTasksAdapter.submitList(mutableTasksListUnCompleteTasks)
                    tasksListsCompletedTasksAdapter.submitList(mutableTasksListCompletedTasks)
                }
            }
        })
    }

    private fun setActionBarTitle(listNameName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = listNameName
    }

    private fun tasksListClickListener(tasksList: TasksList){
        navController.navigate(TasksListsFragmentDirections.actionTasksListsFragmentToTasksListDetailsFragment(tasksList,arguments,argumentsTasksListName.toTypedArray()))
    }

    private fun importantClickListener(tasksList: TasksList){
        tasksListsRepository.renameImportant(tasksList)
    }

    private fun completeTasksClickListener(tasksList: TasksList){
        tasksListsRepository.isAddInCompleteTasksList(tasksList,"true")
    }

    private fun importantCompletedTasksClickListener(tasksList: TasksList){
        tasksListsRepository.renameImportant(tasksList)
    }

    private fun unCompleteTasksListener(tasksList: TasksList){
        tasksListsRepository.isAddInCompleteTasksList(tasksList,"false")
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_overflow_tasks_list,menu)

        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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
            }
            R.id.search -> {
                true
            }
            R.id.renameList -> {
                tasksListsRepository.renameList(databaseReferencePrevious)
                true
            }
            R.id.deleteList -> {
                databaseReferencePrevious.removeValue()
                navController.navigate(TasksListsFragmentDirections.actionTasksListsFragmentToTasksFragment())
                true
            }
            R.id.deleteCompleteList -> {
                tasksListsRepository.deleteCompletedList()
                true
            }
            R.id.deleteAllTasks  -> {
                tasksListsRepository.deleteAllTasks()
                true
            }
            R.id.defaultSort -> {
                tasksListsViewModel.getSortType("Default")
                true
            }
            R.id.nameAsc -> {
                tasksListsViewModel.getSortType("Name ASC")
                true
            }
            R.id.nameDesc -> {
                tasksListsViewModel.getSortType("Name DESC")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterList(query: String?){
        if(query != null){
            val filteredListUnCompleteTasks = ArrayList<TasksList>()
            for (i in mutableTasksListUnCompleteTasks){
                if (i.title.lowercase(Locale.ROOT).contains(query)){
                    filteredListUnCompleteTasks.add(i)
                }
            }

            val filteredListCompleteTasks = ArrayList<TasksList>()
            for (i in mutableTasksListCompletedTasks){
                if (i.title.lowercase(Locale.ROOT).contains(query)){
                    filteredListCompleteTasks.add(i)
                }
            }

            if(filteredListUnCompleteTasks.isEmpty()){
                Toast.makeText(requireContext(),"No Task list found", Toast.LENGTH_SHORT).show()
            }
            else {
                tasksListsUnCompletedTasksAdapter.setFilteredList(filteredListUnCompleteTasks)
            }

            if(filteredListCompleteTasks.isEmpty()){
                Toast.makeText(requireContext(),"No Task list found", Toast.LENGTH_SHORT).show()
            }
            else {
                tasksListsCompletedTasksAdapter.setFilteredList(filteredListCompleteTasks)
            }
        }
    }

}