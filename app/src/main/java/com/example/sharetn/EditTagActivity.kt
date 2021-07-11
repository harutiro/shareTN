package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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

class EditTagActivity : AppCompatActivity(){

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: OriginTagRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tag)

        findViewById<View>(R.id.brEdit).visibility = GONE

        val editTextTextMultiLine = findViewById<EditText>(R.id.editTextTextMultiLine)
        editTextTextMultiLine.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                findViewById<View>(R.id.brEdit).visibility = VISIBLE
            }else{
                findViewById<View>(R.id.brEdit).visibility = GONE
            }
        }

        val id = intent.getStringExtra("id")

        findViewById<ImageButton>(R.id.EditTagSaveButton).setOnClickListener{
            realm.executeTransaction{
                val new = if(id == null){
                    it.createObject(OriginTagDateClass::class.java,UUID.randomUUID().toString())
                }else{
                    it.where(OriginTagDateClass::class.java).equalTo("Id",id).findFirst()
                }

                new?.name = editTextTextMultiLine.text.toString()
            }

            editTextTextMultiLine.setText("")
            rVGo()
        }





        val rView = findViewById<RecyclerView>(R.id.rVTagEdit)
        adapter = OriginTagRecyclerViewAdapter(this , object: OriginTagRecyclerViewAdapter.OnItemClickListner{
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

        rVGo()
    }

    fun rVGo(){

        val mainPersons: RealmResults<OriginTagDateClass> = realm.where(OriginTagDateClass::class.java).findAll()
        adapter?.setList(mainPersons)
    }
}