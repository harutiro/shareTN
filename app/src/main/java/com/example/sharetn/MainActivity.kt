package com.example.sharetn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
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
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import java.util.*


class MainActivity : AppCompatActivity() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: MainRecyclerViewAdapter? = null
    var serchTagChipGroup:ChipGroup? = null

    var tagState:Boolean = true

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

        //TODO: インテントのリザルトコードのより動作を変えるように変更
        if(tagState){
            setChip()
            tagState = false
        }
        recyclerViewGo()

    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

    fun setChip(){
        serchTagChipGroup?.removeAllViews()
        val new = realm.where(OriginTagDateClass::class.java).findAll()

        //アーカイブ部分の特別なチップの作成
        val firstChip = Chip(serchTagChipGroup?.context)

        firstChip.text= "アーカイブ"

        // necessary to get single selection working
        firstChip.isCheckable = true
        firstChip.isClickable = true
        firstChip.checkedIcon = ContextCompat.getDrawable(this,R.drawable.ic_mtrl_chip_checked_black)

        firstChip.setOnCheckedChangeListener { _, _ ->
            recyclerViewGo()
        }

        serchTagChipGroup?.addView(firstChip)

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

        //Realmデータや、検索枠の取得
        val word = findViewById<EditText>(R.id.searchEditText).text.toString()
        var mutablePerson: MutableList<MainDate> = realm.where(MainDate::class.java).findAll().toMutableList()

        Log.d("debug",mutablePerson.size.toString())
        if(mutablePerson.size > 0){
            findViewById<ImageView>(R.id.hintImage).visibility = GONE
            findViewById<TextView>(R.id.hintText).visibility = GONE
        }else{
            findViewById<ImageView>(R.id.hintImage).visibility = VISIBLE
            findViewById<TextView>(R.id.hintText).visibility = VISIBLE
        }

        //一時保存配列の作成
        val mutableNewPerson: MutableList<MainDate> = mutableListOf()
        val stateTagList = mutableListOf<String>()

        //選択されているタグの選択
        for(i in serchTagChipGroup?.checkedChipIds!!){
            stateTagList.add(findViewById<Chip>(i).text.toString())
        }

        //アーカイブの消去
        if(stateTagList.contains("アーカイブ")){
            mutablePerson = mutablePerson.filter{it.archive}.toMutableList()
            stateTagList.remove("アーカイブ")
        }else{
            mutablePerson = mutablePerson.filter{!it.archive}.toMutableList()
        }



        //検索バーでの検索
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

        val tempItemTagList = mutableListOf<String>()
        val realmStateResult = mutableListOf<String>()

        //tagにおける検索
        for(data in mutablePerson){

            for(tag in data.tagList!!){
                tempItemTagList.add(tag.copyId.toString())
            }
            for( item in stateTagList){
                realmStateResult.add(realm.where(OriginTagDateClass::class.java).equalTo("name",item).findFirst()?.id.toString())
            }
            if(tempItemTagList.containsAll(realmStateResult)) mutableNewPerson.add(data)

        }

        adapter?.setList(mutableNewPerson)


    }

    //　アプリバーの部分
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            Snackbar.make(findViewById(android.R.id.content),"現在設定できるものはありません", Snackbar.LENGTH_SHORT).show()

            true
        }

        R.id.create_tag_settings ->{
            tagState = true
            val intent = Intent(this,EditTagActivity::class.java)
            startActivity(intent)

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
}