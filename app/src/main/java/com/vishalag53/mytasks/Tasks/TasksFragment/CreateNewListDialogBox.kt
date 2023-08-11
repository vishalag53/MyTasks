package com.vishalag53.mytasks.Tasks.TasksFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.CreateNewListBinding

class CreateNewListDialogBox: DialogFragment() {

    private lateinit var binding: CreateNewListBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            requireActivity().layoutInflater,
            R.layout.create_new_list,
            null,
            false
        )

        val builder = AlertDialog.Builder(requireContext())

        if(binding.getListName.text.isEmpty()){
            binding.createBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.createBtn))
        }
        else{
            binding.createBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.createBtn1))
        }

        binding.createBtn.setOnClickListener {
            val listName = binding.getListName
            val enteredListName = listName.text.toString()

            (requireParentFragment() as TasksFragment).onNameEntered(enteredListName,binding.getListName)

            dismiss()
        }

        builder.setView(binding.root)
        return  builder.create()
    }

    interface DialogListener{
        fun onNameEntered(listName: String)
    }
}