package com.example.sharetn

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.OriginTagRecyclerViewAdapter
import com.example.sharetn.Adapter.OriginTagSelectRecyclerViewAdapter
import com.example.sharetn.Date.OriginTagDateClass
import com.example.sharetn.Date.TagDateClass
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class SelectTagActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: OriginTagSelectRecyclerViewAdapter? = null
    var stateTagList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_tag)



        val id = intent.getStringExtra("id")
        stateTagList = intent.getStringArrayListExtra("stateTagList") as ArrayList<String>


        val rView = findViewById<RecyclerView>(R.id.selectTagRV)
        adapter = OriginTagSelectRecyclerViewAdapter(this , object: OriginTagSelectRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: OriginTagDateClass,state:Boolean) {

                if(state){
                    stateTagList.addAll(listOf(item.Id.toString()))
                }else{
                    stateTagList.removeAll(listOf(item.Id.toString()))
                }

            }
        })
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter

        rVGo()
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

    //戻るボタンの処理
    override fun onBackPressed() {
        //        タグデータを受け渡す
        val intent = Intent()
        intent.putExtra("stateTagList",stateTagList)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    fun rVGo(){

        val mainPersons: RealmResults<OriginTagDateClass> = realm.where(OriginTagDateClass::class.java).findAll()
        adapter?.setList(mainPersons,stateTagList)
    }
}