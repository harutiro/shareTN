package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.adapter.MainRecyclerViewAdapter
import com.example.sharetn.date.MainDate
import com.example.sharetn.date.OriginTagDateClass
import com.example.sharetn.dousa.JapaneseChange
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.realm.Realm
import java.util.*


class MainActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: MainRecyclerViewAdapter? = null
    var serchTagChipGroup:ChipGroup? = null

   // val stateTagList:MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.searchEditText).doOnTextChanged{ _, _, _, _ ->
            recyclerViewGo()
        }

        serchTagChipGroup = findViewById(R.id.serchTagChipGroup)

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.puraFAB).setOnClickListener{
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.dellButton).setOnClickListener{
            findViewById<EditText>(R.id.searchEditText).setText("")
            recyclerViewGo()
        }
        findViewById<Button>(R.id.testButton).setOnClickListener{
            val intent = Intent(this,EditTagActivity::class.java)
            startActivity(intent)
        }

        val rView = findViewById<RecyclerView>(R.id.RView)
        adapter = MainRecyclerViewAdapter(this , object: MainRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: MainDate) {
                // SecondActivityに遷移するためのIntent
                val intent = Intent(applicationContext, ViewActivity::class.java)
                // RecyclerViewの要素をタップするとintentによりSecondActivityに遷移する
                // また，要素のidをSecondActivityに渡す
                intent.putExtra("id", item.id)
                startActivity(intent)
            }
        })
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter
        recyclerViewGo()
    }


    override fun onResume(){
        super.onResume()

        recyclerViewGo()
        setChip()

    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

    fun setChip(){
        serchTagChipGroup?.removeAllViews()
        val new = realm.where(OriginTagDateClass::class.java).findAll()
        for (i in new) {

            val chip = Chip(serchTagChipGroup?.context)

            chip.text= i.name

            // necessary to get single selection working
            chip.isCheckable = true
            chip.isClickable = true
            chip.checkedIcon = ContextCompat.getDrawable(this,R.drawable.ic_mtrl_chip_checked_black)

            chip.setOnCheckedChangeListener { _, _ ->
                recyclerViewGo()
            }

            serchTagChipGroup?.addView(chip)
        }
    }


    @Suppress("DEPRECATION")
    fun recyclerViewGo(){

        val word = findViewById<EditText>(R.id.searchEditText).text.toString()
        var mutablePerson: MutableList<MainDate> = realm.where(MainDate::class.java).findAll().toMutableList()

        val mutableNewPerson: MutableList<MainDate> = mutableListOf()
        val stateTagList = mutableListOf<String>()

        for(i in serchTagChipGroup?.checkedChipIds!!){
            stateTagList.add(findViewById<Chip>(i).text.toString())
        }

        if(word != ""){
            val all = word.split(" ","　")

            for(p in all){

                //カタカナは全部ひらがなに変換する
                //大文字の英語は小文字の英語に変換する
                val filterMain = mutablePerson.filter{Regex(JapaneseChange().converted(p.toLowerCase(Locale.ROOT)))
                                        .containsMatchIn(JapaneseChange().converted(it.mainText.toLowerCase(Locale.ROOT)))
                }

                val filterMemo = mutablePerson.filter{Regex(JapaneseChange().converted(p.toLowerCase(Locale.ROOT)))
                                        .containsMatchIn(JapaneseChange().converted(it.memoText.toLowerCase(Locale.ROOT)))}

                mutablePerson = (filterMain + filterMemo) as MutableList<MainDate>


            }
        }




        for(data in mutablePerson){

            val tempItemTagList = mutableListOf<String>()
            for(tag in data.tagList!!){
                tempItemTagList.add(tag.copyId.toString())
            }
            val realmStateResult = mutableListOf<String>()
            for( item in stateTagList){
                realmStateResult.add(realm.where(OriginTagDateClass::class.java).equalTo("name",item).findFirst()?.id.toString())
            }
            if(tempItemTagList.containsAll(realmStateResult)) mutableNewPerson.add(data)

        }



        adapter?.setList(mutableNewPerson)


    }
}