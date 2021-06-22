package com.example.sharetn.Date

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class MainDate(
    @PrimaryKey open var Id: String? = UUID.randomUUID().toString(),
    open var icon: String = "",
    open var mainText: String = "",
    open var subText: String = "",
    open var image: String = "",
    open var tagList: RealmList<TagDateClass>? = null,
    open var memoText: String = "",
    open var dayText: String = ""
        ): RealmObject()