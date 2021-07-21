package com.example.sharetn

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.OriginTagDateClass
import com.example.sharetn.Date.TagDateClass
import com.example.sharetn.dousa.JapaneseChange
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ViewActivity : AppCompatActivity() {

    val REQUEST_CODE = 1000

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
    var layoutId:ConstraintLayout? = null

    var stateTagList: ArrayList<String>? = ArrayList<String>()

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
        val id = intent.getStringExtra("id")

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

        Log.d("debug",Regex("https://").containsMatchIn(item.subText) .toString())
        Log.d("debug",Regex("http://").containsMatchIn(item.subText) .toString())

        //＝＝＝＝＝＝＝＝＝＝＝＝URLじゃなかった場合URL部分の表示を消す＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        if(!(Regex("http://").containsMatchIn(item.subText) || Regex("https://").containsMatchIn(item.subText))){
            Log.d("debug","caome")
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

    fun setChip(){
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

    override fun onActivityResult(requestCode:Int, resultCode: Int, date: Intent?) {
        super.onActivityResult(requestCode, resultCode, date)


        if(requestCode == 1000) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    stateTagList = date?.getStringArrayListExtra("stateTagList")

                    setChip()
                }

                else -> {
                }
            }
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

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}