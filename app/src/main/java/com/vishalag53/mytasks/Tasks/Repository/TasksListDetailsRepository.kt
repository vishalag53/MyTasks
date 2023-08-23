package com.vishalag53.mytasks.Tasks.Repository

import android.content.Context
import com.google.firebase.database.DatabaseReference

class TasksListDetailsRepository (
    private val requireContext: Context,
    private val databaseReferencePrevious: DatabaseReference,
    private val databaseReference: DatabaseReference
) {
}