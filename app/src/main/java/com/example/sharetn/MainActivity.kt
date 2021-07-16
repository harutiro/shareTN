package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.OriginTagDateClass
import com.example.sharetn.dousa.JapaneseChange
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.realm.Realm
import io.realm.RealmResults
import java.util.*


class MainActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: MainRecyclerViewAdapter? = null
    var serchTagChipGroup:ChipGroup? = null

    val stateTagList:MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        serchTagChipGroup = findViewById<ChipGroup>(R.id.serchTagChipGroup)

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
        findViewById<Button>(R.id.testButton).setOnClickListener{
            val intent = Intent(this,EditTagActivity::class.java)
            startActivity(intent)
        }

        setChip()




        val rView = findViewById<RecyclerView>(R.id.RView)
        adapter = MainRecyclerViewAdapter(this , object: MainRecyclerViewAdapter.OnItemClickListner{
            override fun onItemClick(item: MainDate) {
                // SecondActivityに遷移するためのIntent
                val intent = Intent(applicationContext, EditActivity::class.java)
                // RecyclerViewの要素をタップするとintentによりSecondActivityに遷移する
                // また，要素のidをSecondActivityに渡す
                intent.putExtra("id", item.id)
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

            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.d("debag",buttonView.text.toString())
                Log.d("debag",isChecked.toString())

                if(isChecked){
                    stateTagList.addAll(listOf(i.id.toString()))
                }else{
                    stateTagList.removeAll(listOf(i.id.toString()))
                }
                RVGo()

            }

            serchTagChipGroup?.addView(chip)
        }
    }


    @Suppress("DEPRECATION")
    fun RVGo(){

        val word = findViewById<EditText>(R.id.searchEditText).text.toString()
        val mainPersons: RealmResults<MainDate> = realm.where(MainDate::class.java).findAll()

        var mainPerson: MutableList<MainDate> = mutableListOf()
        for (person in mainPersons){
            mainPerson.add(person)
        }


        for(i in stateTagList){
            outer@for(j in mainPerson){
                for(tag in j.tagList!!){

                    Log.d("debag",tag.copyId.toString())
                    Log.d("debag",i)
                    if(tag.copyId != i){
                        Log.d("debag","Ok")
                        mainPerson.remove(j)

                        break@outer
                    }
                }
            }
        }

        if(word != ""){
            val all = word.split(" ","　")

            for(p in all){

                //カタカナは全部ひらがなに変換する
                //大文字の英語は小文字の英語に変換する
                val filterMain = mainPerson.filter{Regex(JapaneseChange().converted(p.toLowerCase(Locale.ROOT)))
                                        .containsMatchIn(JapaneseChange().converted(it.mainText.toLowerCase(Locale.ROOT)))
                }

                val filterMemo = mainPerson.filter{Regex(JapaneseChange().converted(p.toLowerCase(Locale.ROOT)))
                                        .containsMatchIn(JapaneseChange().converted(it.memoText.toLowerCase(Locale.ROOT)))}

                mainPerson = (filterMain + filterMemo) as MutableList<MainDate>


            }
        }

        adapter?.setList(mainPerson)


    }
}