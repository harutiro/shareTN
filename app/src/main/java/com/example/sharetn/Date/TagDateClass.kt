package com.example.sharetn.Date

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

class TagDateClass (
    @PrimaryKey open var Id: String? = UUID.randomUUID().toString(),
    val Icon:Int = 0,
    val name:String = ""

    ): RealmObject()