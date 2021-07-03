package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import io.realm.Realm
import io.realm.RealmResults

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

        findViewById<ImageButton>(R.id.searchButton).setOnClickListener{
            RVGo(findViewById<EditText>(R.id.searchEditText).text.toString())
        }
        findViewById<ImageButton>(R.id.dellButton).setOnClickListener{
            findViewById<EditText>(R.id.searchEditText).setText("")
        }



        RVGo()
    }

    override fun onResume(){
        super.onResume()

        RVGo()

    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

    fun RVGo(word:String = ""){


        val mainPersons: RealmResults<MainDate> = if(word == ""){
            realm.where(MainDate::class.java).findAll()
        } else{
            realm.where(MainDate::class.java)
                .contains("mainText",word)
                .or()
                .contains("subText",word)
                .or()
                .contains("memoText",word)
                .findAll()
        }
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
}