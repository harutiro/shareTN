package com.example.sharetn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.TagDateClass
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //realmのインスタンス
        val realm: Realm = Realm.getDefaultInstance()

//        realm.executeTransaction {
//
//            val mainObject = it.createObject(MainDate::class.java, UUID.randomUUID().toString()).apply {
//                this.icon = R.drawable.ic_baseline_more_vert_24
//                this.mainText = "main"
//                this.subText = "sub"
//                this.image = ""
//
//                val tagObject = it.createObject(TagDateClass::class.java ,UUID.randomUUID().toString()).apply {
//                    this.Icon = R.drawable.ic_baseline_more_vert_24
//                    this.name = "タグ"
//                    this.color = ""
//                    this.mojiColor = ""
//                }
//                val tagObject1 = it.createObject(TagDateClass::class.java ,UUID.randomUUID().toString()).apply {
//                    this.Icon = R.drawable.ic_baseline_more_vert_24
//                    this.name = "タグ"
//                    this.color = ""
//                    this.mojiColor = ""
//                }
//                val tagObject2 = it.createObject(TagDateClass::class.java ,UUID.randomUUID().toString()).apply {
//                    this.Icon = R.drawable.ic_baseline_more_vert_24
//                    this.name = "タグ"
//                    this.color = ""
//                    this.mojiColor = ""
//                }
//
//                this.tagList?.add(tagObject)
//                this.tagList?.add(tagObject1)
//                this.tagList?.add(tagObject2)
//            }
//        }



//        val courseDate: List <MainDate> = listOf(
//            MainDate(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者","aaaaa","", null),
//            MainDate(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者","aaaaa","", null),
//            MainDate(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者","aaaaa","", null),
//            MainDate(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者","aaaaa","", null)
//
//        )

        val mainPersons: RealmResults<MainDate> = realm.where(MainDate::class.java).findAll()
        val RView = findViewById<RecyclerView>(R.id.RView)
        val adapter = MainRecyclerViewAdapter(this)
        RView.layoutManager = LinearLayoutManager(this)
        RView.adapter = adapter

        //リサイクラービューアダプターで宣言したaddAllメソッドを呼んであげてデータも渡している
        adapter.addAll(mainPersons)
    }
}