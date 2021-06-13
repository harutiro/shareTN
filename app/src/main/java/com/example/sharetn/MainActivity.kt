package com.example.sharetn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val courseDate: List <MainDate> = listOf(
            MainDate(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者","aaaaa","", null)

        )


        val RView = findViewById<RecyclerView>(R.id.RView)

        val adapter = MainRecyclerViewAdapter(this)
        RView.layoutManager = LinearLayoutManager(this)
        RView.adapter = adapter

        //リサイクラービューアダプターで宣言したaddAllメソッドを呼んであげてデータも渡している
        adapter.addAll(courseDate)
    }
}