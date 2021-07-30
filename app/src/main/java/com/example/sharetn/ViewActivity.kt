package com.example.sharetn

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sharetn.date.MainDate
import com.example.sharetn.date.OriginTagDateClass
import com.example.sharetn.date.TagDateClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import java.util.*


class ViewActivity : AppCompatActivity() {

    private val requestCode = 1000

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var subText:TextView? =  null
    var subIcon:ImageView? =  null
    var mainText:TextView? = null
    var mainIcon:ImageView? = null
    var dayText:TextView? =  null
    var dayIcon:ImageView? =  null
    var memoIcon:ImageView? = null
    var memoText:TextView? = null
    var image:ImageView? =    null
    var editTagChipGroup:ChipGroup? = null
    private var layoutId:ConstraintLayout? = null

    var stateTagList: ArrayList<String>? = ArrayList<String>()

    var id = ""

    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        title = "詳細"

        subText = findViewById(R.id.subText)
        subIcon = findViewById(R.id.subIcon)
        mainText = findViewById(R.id.mainText)
        mainIcon = findViewById(R.id.mainIcon)
        dayText = findViewById(R.id.dayText)
        dayIcon = findViewById(R.id.dayIcon)
        memoIcon = findViewById(R.id.memoIcon)
        memoText = findViewById(R.id.memoText)
        image = findViewById(R.id.image)
        editTagChipGroup = findViewById(R.id.editTagChipGroup)
        layoutId = findViewById(R.id.viewConstraintLayout)

        // MainActivityのRecyclerViewの要素をタップした場合はidが，fabをタップした場合は"空白"が入っているはず
        id = intent.getStringExtra("id").toString()

        subText?.setOnClickListener{
            copyToClipboard(subText?.text.toString())
        }
        mainText?.setOnClickListener{
            copyToClipboard(mainText?.text.toString())
        }
        memoText?.setOnClickListener{
            copyToClipboard(memoText?.text.toString())
        }

        findViewById<FloatingActionButton>(R.id.editFAB).setOnClickListener{
            val intent = Intent(this,EditActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.itemRemoveText).setOnClickListener{
            val person = realm.where(MainDate::class.java).equalTo("id",id).findFirst()

            realm.executeTransaction {
                person?.deleteFromRealm()
            }

            finish()

        }

        //データのはめ込み
        val item = realm.where(MainDate::class.java).equalTo("id", id).findFirst()

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝BASE６４の画像はめ込み＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        val decodedByte: ByteArray = Base64.decode(item?.icon, 0)
        mainIcon?.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size))

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝その他はめ込み＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        dayText?.text = item?.dayText
        memoText?.text = item?.memoText
        mainText?.text = item?.mainText
        subText?.text = item?.subText

        //＝＝＝＝＝＝＝＝＝＝＝＝タグのはめ込み部分＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        for(i in item?.tagList!!){
            stateTagList?.add(i.copyId.toString())
        }
        setChip()

        //＝＝＝＝＝＝＝＝＝＝＝＝URLじゃなかった場合URL部分の表示を消す＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        if(!(Regex("http://").containsMatchIn(item.subText) || Regex("https://").containsMatchIn(item.subText))){
            subText?.visibility = GONE
            subIcon?.visibility = GONE
        }



    }

    private fun copyToClipboard(text: String?) {
        //クリップボードの保存
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", text)
        clipboard.setPrimaryClip(clip)

        //スナックバーの表示
        if(layoutId != null){
            Snackbar.make(layoutId!!,"コピーしました", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun setChip(){
        editTagChipGroup?.removeAllViews()
        for (index in stateTagList!!) {
            val new = realm.where(OriginTagDateClass::class.java).equalTo("id",index).findFirst()

            val chip = Chip(editTagChipGroup?.context)
            chip.text= new?.name
            chip.isClickable = false
            editTagChipGroup?.addView(chip)
        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

//    タグインテントにおける戻りデータの受け取り部分
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent = result.data

            stateTagList = intent?.getStringArrayListExtra("stateTagList")
            setChip()
        }
    }

    
//　アプリバーの部分
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            //ボトムシートを上に浮き上がらせる
            val view = findViewById<ConstraintLayout>(R.id.view_bottom_sheet)
            val mBottomSheetBehavior = BottomSheetBehavior.from(view)
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            true
        }

        R.id.archive_settings -> {
            val item = realm.where(MainDate::class.java).equalTo("id", id).findFirst()

            realm.executeTransaction {
                item?.archive = !item?.archive!!
            }

            if(item?.archive == true){
                Snackbar.make(findViewById(android.R.id.content),"アーカイブしました", Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(findViewById(android.R.id.content),"アーカイブを解除しました", Snackbar.LENGTH_SHORT).show()
            }

            invalidateOptionsMenu()

            true
        }

        R.id.tag_settings ->{
            //タグへのインテント
            val intent = Intent(this , SelectTagActivity::class.java)
            intent.putExtra("stateTagList",stateTagList)
            launcher.launch(intent)

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
        inflater.inflate(R.menu.view_activity_menu, menu)

        val menuArchive = menu?.findItem(R.id.archive_settings)
        val person = realm.where(MainDate::class.java).equalTo("id", id).findFirst()

        if(person?.archive == true){
            menuArchive?.setIcon(R.drawable.unarchive_black_24dp)
        }else{
            menuArchive?.setIcon(R.drawable.archive_black_24dp)
        }


        return super.onCreateOptionsMenu(menu)
    }

    //戻るボタンの処理
    override fun onBackPressed() {
        realm.executeTransaction{
            val new = it.where(MainDate::class.java).equalTo("id",id).findFirst()

            //＝＝＝＝＝＝＝＝＝＝＝＝タグの保存===================
            new?.tagList?.clear()
            for( i in stateTagList!!){
                val tagObject = realm.createObject(TagDateClass::class.java ,UUID.randomUUID().toString()).apply {
                    this.copyId = i
                }
                new?.tagList?.add(tagObject)
            }
        }

        finish()
    }


}