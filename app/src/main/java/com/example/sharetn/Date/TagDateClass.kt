package com.example.sharetn.Date

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class TagDateClass (
    @PrimaryKey open var Id: String? = UUID.randomUUID().toString(),
    open var Icon:Int = 0,
    open var name:String = "",
    open var color:String = "",
    open var mojiColor:String = ""

    ): RealmObject()