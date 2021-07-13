package com.example.sharetn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

    // キーボード表示を制御するためのオブジェクト
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var container: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tag)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        container = findViewById(R.id.constraintEditTag)


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


                if(editTextTextMultiLine.text.toString() != ""){
                    val new = if(id == null){
                        it.createObject(OriginTagDateClass::class.java,UUID.randomUUID().toString())
                    }else{
                        it.where(OriginTagDateClass::class.java).equalTo("Id",id).findFirst()
                    }

                    new?.name = editTextTextMultiLine.text.toString()
                }
            }

            editTextTextMultiLine.setText("")
            rVGo()
        }





        val rView = findViewById<RecyclerView>(R.id.rVTagEdit)
        adapter = OriginTagRecyclerViewAdapter(this , object: OriginTagRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: OriginTagDateClass) {
//                // SecondActivityに遷移するためのIntent
//                val intent = Intent(applicationContext, EditActivity::class.java)
//                // RecyclerViewの要素をタップするとintentによりSecondActivityに遷移する
//                // また，要素のidをSecondActivityに渡す
//                intent.putExtra("id", item.Id)
//                startActivity(intent)
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

    // 画面タップ時に呼ばれる
    override fun onTouchEvent(event: MotionEvent): Boolean {

        // キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(container.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        // 背景にフォーカスを移す
        container.requestFocus()

        return false
    }
}