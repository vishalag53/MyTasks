package com.vishalag53.mytasks.Tasks.data

import java.io.Serializable

data class TasksList(
    val id: String,
    var title: String,
    var details: String?,
    var date: String?,
    var time: String?,
    var repeat: String?,
    var important: String,
    var isCompleted: String
): Serializable