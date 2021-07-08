package com.example.sharetn

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import com.example.sharetn.dousa.JapaneseChange
import io.realm.Realm
import io.realm.RealmResults


class MainActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: MainRecyclerViewAdapter? = null

    @RequiresApi(Build.VERSION_CODES.Q)
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
            RVGo()
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume(){
        super.onResume()

        RVGo()

    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }


    @Suppress("DEPRECATION")
    fun RVGo(){

        val word = findViewById<EditText>(R.id.searchEditText).text.toString()
        val mainPersons: RealmResults<MainDate> = realm.where(MainDate::class.java).findAll()

        var mainPerson: MutableList<MainDate> = mainPersons

        if(word != ""){
            val all = word.split(" ","　")

            for(p in all){

                //カタカナは全部ひらがなに変換する
                //大文字の英語は小文字の英語に変換する
                val filterMain = mainPerson.filter{Regex(JapaneseChange().converted(p.toLowerCase()))
                                        .containsMatchIn(JapaneseChange().converted(it.mainText.toLowerCase()))}

                val filterMemo = mainPerson.filter{Regex(JapaneseChange().converted(p.toLowerCase()))
                                        .containsMatchIn(JapaneseChange().converted(it.memoText.toLowerCase()))}

                mainPerson = (filterMain + filterMemo) as MutableList<MainDate>


            }
            //リサイクラービューアダプターで宣言したaddAllメソッドを呼んであげてデータも渡している
            adapter?.setList(mainPerson)
        }else{
            adapter?.setList(mainPersons)

        }


    }
}