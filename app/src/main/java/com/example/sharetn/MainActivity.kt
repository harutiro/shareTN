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
import com.example.sharetn.dousa.BottomSheetFragment
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

        findViewById<Button>(R.id.button).setOnClickListener{
            val myBottomSheet = BottomSheetFragment()
            myBottomSheet.show(supportFragmentManager,"navigation_bottom_sheet")
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.puraFAB).setOnClickListener{
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }


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

    override fun onResume(){
        super.onResume()

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