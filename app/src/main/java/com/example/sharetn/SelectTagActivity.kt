package com.example.sharetn

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.OriginTagRecyclerViewAdapter
import com.example.sharetn.Adapter.OriginTagSelectRecyclerViewAdapter
import com.example.sharetn.Date.OriginTagDateClass
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class SelectTagActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: OriginTagSelectRecyclerViewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_tag)

//        val stateTagList:

        val id = intent.getStringExtra("id")


        val rView = findViewById<RecyclerView>(R.id.selectTagRV)
        adapter = OriginTagSelectRecyclerViewAdapter(this , object: OriginTagSelectRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: OriginTagDateClass,state:Boolean) {
                Log.d("debag",state.toString())
                Log.d("debag",item.name.toString())

                if(state){

                }
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