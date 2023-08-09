package com.vishalag53.mytasks.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import com.vishalag53.mytasks.R
import com.vishalag53.mytasks.databinding.FragmentLogInStartBinding


class LogInStartFragment : Fragment() {

    private lateinit var binding: FragmentLogInStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLogInStartBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            backBtnAction()
        }
    }

    private fun backBtnAction() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Not Sign In")
            .setMessage("Are you sure, don't want to log in or sign in")
            .setPositiveButton("YES"){ dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Cancle"){ dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)

        alertDialogBuilder.create().show()
    }

}