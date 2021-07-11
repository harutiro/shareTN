package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Adapter.OriginTagRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.OriginTagDateClass
import com.example.sharetn.Date.TagDateClass
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class EditTagActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tag)

        val id = intent.getStringExtra("id")

        findViewById<ImageButton>(R.id.EditTagSaveButton).setOnClickListener{
            realm.executeTransaction{
                val new = if(id == null){
                    it.createObject(OriginTagDateClass::class.java,UUID.randomUUID().toString())
                }else{
                    it.where(OriginTagDateClass::class.java).equalTo("Id",id).findFirst()
                }

                new?.name = findViewById<EditText>(R.id.editTextTextMultiLine).text.toString()
            }

            findViewById<EditText>(R.id.editTextTextMultiLine).setText("")
        }



        val rView = findViewById<RecyclerView>(R.id.rVTagEdit)
        val adapter = OriginTagRecyclerViewAdapter(this , object: OriginTagRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: OriginTagDateClass) {
                // SecondActivityに遷移するためのIntent
                val intent = Intent(applicationContext, EditActivity::class.java)
                // RecyclerViewの要素をタップするとintentによりSecondActivityに遷移する
                // また，要素のidをSecondActivityに渡す
                intent.putExtra("id", item.Id)
                startActivity(intent)
            }
        })
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter

        val mainPersons: RealmResults<OriginTagDateClass> = realm.where(OriginTagDateClass::class.java).findAll()
        adapter?.setList(mainPersons)
    }
}