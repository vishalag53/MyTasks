package com.vishalag53.mytasks.Tasks.data

import java.io.Serializable

data class tasksList(
    val id: String,
    val title: String,
    val details: String?,
    val date: String?,
    val time: String?,
    val repeat: String?,
    val important: Boolean
): Serializable
