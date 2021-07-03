package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    var adapter: MainRecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.puraFAB).setOnClickListener{
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.searchButton).setOnClickListener{
            RVGo()
        }
        findViewById<ImageButton>(R.id.dellButton).setOnClickListener{
            findViewById<EditText>(R.id.searchEditText).setText("")
        }


        val rView = findViewById<RecyclerView>(R.id.RView)
        adapter = MainRecyclerViewAdapter(this , object: MainRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: MainDate) {
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

        val mainPersons: RealmResults<MainDate> = realm.where(MainDate::class.java).findAll()
        adapter?.setList(mainPersons)
    }

    override fun onResume(){
        super.onResume()

        RVGo()

    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

    fun RVGo(){

        val word = findViewById<EditText>(R.id.searchEditText).text.toString()
        val mainPersons: RealmResults<MainDate> = realm.where(MainDate::class.java).findAll()

        var mainPerson: MutableList<MainDate> = mainPersons

        if(word != ""){
            val all = word.split(" ","　")

            for(p in all){
                val filterMain = mainPerson.filter{Regex(p).containsMatchIn(it.mainText)}
                val filterMemo = mainPerson.filter{Regex(p).containsMatchIn(it.memoText)}

                mainPerson = (filterMain + filterMemo) as MutableList<MainDate>


            }

            //TODO: or検索になっているものをAND検索にする。
            //TODO: 二重に重なっているものをなくす
            //リサイクラービューアダプターで宣言したaddAllメソッドを呼んであげてデータも渡している
            adapter?.setList(mainPerson)
        }else{
            adapter?.setList(mainPersons)

        }


    }
}