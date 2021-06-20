package com.example.sharetn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.TagDateClass
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class MainActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.puraFAB).setOnClickListener{
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

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
        val adapter = MainRecyclerViewAdapter(this , object: MainRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: MainDate) {
                // SecondActivityに遷移するためのIntent
                val intent = Intent(applicationContext, EditActivity::class.java)
                // RecyclerViewの要素をタップするとintentによりSecondActivityに遷移する
                // また，要素のidをSecondActivityに渡す
                intent.putExtra("id", item.Id)
                startActivity(intent)
            }
        })
        RView.layoutManager = LinearLayoutManager(this)
        RView.adapter = adapter

        //リサイクラービューアダプターで宣言したaddAllメソッドを呼んであげてデータも渡している
        adapter.addAll(mainPersons)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }
}