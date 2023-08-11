package com.vishalag53.mytasks.Tasks.TasksFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vishalag53.mytasks.Tasks.Util.menuDelete
import com.vishalag53.mytasks.Tasks.Util.menuDoubleDelete
import com.vishalag53.mytasks.Tasks.Util.setExpandBtnFunction
import com.vishalag53.mytasks.Tasks.data.NameList
import com.vishalag53.mytasks.databinding.FragmentTasksBinding
import com.vishalag53.mytasks.login.LogInActivity

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var newListName: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var adapter: TasksFragmentAdapter
    private lateinit var mutableNameList: MutableList<NameList>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()

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

        binding.clAddNewList.setOnClickListener {
            val createNewListDialogBox = CreateNewListDialogBox()
            createNewListDialogBox.show(childFragmentManager,"CustomDialog")
        }
    }

    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                mutableNameList.clear()
                for (taskSnapshot in snapshot.children){
                    val taskList = taskSnapshot.key?.let {
                        NameList(it,taskSnapshot.value.toString())
                    }

                    if (taskList != null){
                        mutableNameList.add(taskList)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun init(view: View) {
        binding.lifecycleOwner = viewLifecycleOwner
        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("Tasks")
            .child((firebaseAuth.currentUser?.uid.toString()))

        mutableNameList = mutableListOf()
        adapter = TasksFragmentAdapter(mutableNameList, ::deleteItemFromFirebase, ::renameItemInFirebase)
        binding.rvTasks.adapter = adapter
    }

    private fun renameItemInFirebase(nameList: NameList,newName: String){
        databaseReference.child(nameList.listNameId).setValue(newName).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Rename Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d("VISHAL AGRAWAL","Rename ${it.exception.toString()}")
            }
        }

    }

    private fun deleteItemFromFirebase(itemToDelete: NameList) {
        databaseReference.child(itemToDelete.listNameId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d("VISHAL AGRAWAL","Delete ${it.exception.toString()}")
            }
        }
    }

    fun onNameEntered(listName: String, listName1: EditText){
        newListName = listName
        databaseReference.push().setValue(listName).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(requireContext(),"Created  $listName",Toast.LENGTH_SHORT).show()
                listName1.text = null
            }
            else{
                Log.d("VISHAL AGRAWAL","${it.exception?.message}")
            }
        }
    }

}