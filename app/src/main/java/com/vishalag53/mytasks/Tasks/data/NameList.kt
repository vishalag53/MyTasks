package com.vishalag53.mytasks.Tasks.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NameList(
    val listNameId: String,
    val listNameName: String
): Parcelable
