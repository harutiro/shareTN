package com.example.sharetn.Date

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

class MainDate(
    @PrimaryKey open var Id: String? = UUID.randomUUID().toString(),
    val icon: Int = 0,
    val mainText: String = "",
    val subText: String = "",
    val image: Int = 0,
    val tagList: RealmList<TagDateClass>? = null
        ): RealmObject()