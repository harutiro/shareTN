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
import kotlin.String as String1


class EditActivity : AppCompatActivity() {

    val REQUEST_CODE = 1000

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    var subEdit:EditText? =  null
    var subIcon:ImageView? =  null
    var mainEdit:EditText? = null
    var mainIcon:ImageView? = null
    var dayText:TextView? =  null
    var dayIcon:ImageView? =  null
    var memoIcon:ImageView? = null
    var memoEdit:EditText? = null
    var image:ImageView? =    null
    var editTagChipGroup:ChipGroup? = null

    var id: String1? = ""

    var stateTagList: ArrayList<String1>? = ArrayList<String1>()

    var stateEditMode: Boolean = false


//    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setTitle( "編集" )

        subEdit = findViewById<EditText>(R.id.subEdit)
        subIcon = findViewById<ImageView>(R.id.subIcon)
        mainEdit = findViewById<EditText>(R.id.mainEdit)
        mainIcon = findViewById<ImageView>(R.id.mainIcon)
        dayText = findViewById<TextView>(R.id.dayText)
        dayIcon = findViewById<ImageView>(R.id.dayIcon)
        memoIcon = findViewById<ImageView>(R.id.memoIcon)
        memoEdit = findViewById<EditText>(R.id.memoEdit)
        image = findViewById<ImageView>(R.id.image)
        editTagChipGroup = findViewById<ChipGroup>(R.id.editTagChipGroup)

        //スクロールできるように設定
        mainEdit?.movementMethod = ScrollingMovementMethod()
        subEdit?.movementMethod = ScrollingMovementMethod()
        memoEdit?.movementMethod = ScrollingMovementMethod()

        // MainActivityのRecyclerViewの要素をタップした場合はidが，fabをタップした場合は"空白"が入っているはず
        id = intent.getStringExtra("id")
        stateEditMode = intent.getBooleanExtra("editMode",false)

        //============================タグセレクトへのインテント＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        findViewById<TextView>(R.id.tagSelecetTextView).setOnClickListener{
            val intent = Intent(this , SelectTagActivity::class.java)
            intent.putExtra("stateTagList",stateTagList)
            startActivityForResult(intent,REQUEST_CODE)
        }

        //データのはめ込み
        if (id == null){

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝日付のはめ込み＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            val date = Date(System.currentTimeMillis())
            val df = SimpleDateFormat("yyyy年 MM月 dd日", Locale.JAPANESE)
            val formatted = df.format(date)
            dayText?.text = formatted

        }else{

            val item = realm.where(MainDate::class.java).equalTo("id", id).findFirst()

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝BASE６４の画像はめ込み＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            val decodedByte: ByteArray = Base64.decode(item?.icon, 0)
            mainIcon?.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size))

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝その他はめ込み＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            dayText?.text = item?.dayText
            memoEdit?.setText(item?.memoText)
            mainEdit?.setText(item?.mainText)
            subEdit?.setText(item?.subText)

            //＝＝＝＝＝＝＝＝＝＝＝＝タグのはめ込み部分＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            for(i in item?.tagList!!){
                stateTagList?.add(i.copyId.toString())
            }
            setChip()
        }

        //＝＝＝＝＝＝＝＝＝＝＝＝URLじゃなかった場合URL部分の表示を消す＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        if(!Regex("http://").containsMatchIn(subEdit?.text.toString()) && !Regex("https://").containsMatchIn(subEdit?.text.toString())){
            subEdit?.visibility = GONE
            subIcon?.visibility = GONE
        }





//＝＝＝＝＝＝＝＝＝＝＝＝＝＝Realm保存部分
        findViewById<FloatingActionButton>(R.id.saveFAB).setOnClickListener {
            if (mainEdit?.text.toString() == "") {
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"タイトルが入力されていません。", Snackbar.LENGTH_SHORT)
                snackbar.view.setBackgroundResource(R.color.error)
                snackbar.show()
            }else{
                save()
            }
        }




//=============================共有で飛ばされたときに動く部分
        if (TextUtils.equals(intent.action, Intent.ACTION_SEND)) {
            val extras = intent.extras
            val comeText = extras!!.getCharSequence(Intent.EXTRA_TEXT).toString()

            subEdit?.visibility = VISIBLE
            subIcon?.visibility = VISIBLE

            //URLや文字の受け取り
            val domein: String1 = comeText.removePrefix("https://").removePrefix("http://").split("/")[0]

            findViewById<EditText>(R.id.subEdit).setText(comeText)
            findViewById<EditText>(R.id.mainEdit).setText("NowLoading...")




            //URLで動く部分
            if(Regex("http://").containsMatchIn(comeText) || Regex("https://").containsMatchIn(comeText)) {

                //faviconの取得
                Picasso.get()
                    //画像URL
                    .load("https://www.google.com/s2/favicons?domain=$domein")
                    .resize(300, 300) //表示サイズ指定
                    .centerCrop() //resizeで指定した範囲になるよう中央から切り出し
                    .into(findViewById<ImageView>(R.id.mainIcon)) //imageViewに流し込み


                //WebViewでのタイトルの取得の仕方。
                val webview = WebView(this)

                //URL
                webview.loadUrl(comeText)

                //画面を取得するときにここがないと取得できない
                val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val params = WindowManager.LayoutParams(
                    300,
                    300,
                    WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
                )

//                TODO:終わらせるときにクローズさせるようにする。
                wm.addView(webview, params)

                //非表示
                webview.visibility = GONE

                //タイトルの取得
                webview.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String1) {

                        val title = webview.title.toString()
                        mainEdit?.setText(title)


                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String1?,
                        url: String1?
                    ){
                        mainEdit?.setText("ネットワークエラー")
                    }

                }
            }
        }






    }

    fun save(){
        realm.executeTransaction{

            val new = if(id == null){
                it.createObject(MainDate::class.java,UUID.randomUUID().toString())
            }else{
                it.where(MainDate::class.java).equalTo("id",id).findFirst()
            }

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝BASE６４＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            //データ受け取り
            val bmp = (mainIcon?.drawable as BitmapDrawable).bitmap
            //エンコード
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            val output = Base64.encodeToString(b, Base64.NO_WRAP)

            //====================その他データの保存=========================
            new?.icon = output
            new?.mainText = mainEdit?.text.toString()
            new?.subText = subEdit?.text.toString()
            new?.memoText = memoEdit?.text.toString()
            new?.image = ""

            //=====================日付==========================
            val date = Date(System.currentTimeMillis())
            val df = SimpleDateFormat("yyyy年 MM月 dd日", Locale.JAPANESE)
            val formatted = df.format(date)
            new?.dayText = formatted

            //＝＝＝＝＝＝＝＝＝＝＝＝タグの保存===================
            new?.tagList?.clear()
            for( i in stateTagList!!){
                val tagObject = realm.createObject(TagDateClass::class.java ,UUID.randomUUID().toString()).apply {
                    this.copyId = i
                }
                new?.tagList?.add(tagObject)
            }

            finish()
        }
    }

    fun setChip(){
        editTagChipGroup?.removeAllViews()
        for (index in stateTagList!!) {
            val new = realm.where(OriginTagDateClass::class.java).equalTo("id",index).findFirst()


            val chip = Chip(editTagChipGroup?.context)
            chip.text= new?.name

            // necessary to get single selection working
            chip.isCloseIconVisible = true

            chip.setOnCloseIconClickListener {
                stateTagList!!.remove(index)
                setChip()
            }
            editTagChipGroup?.addView(chip)
        }
    }

    //戻るボタンの処理
    override fun onBackPressed() {
        // 行いたい処理
        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
            .setTitle("ホームへ戻る")
            .setMessage("入力したデータを保存しないでホームに戻りますか？")
            .setPositiveButton("OK") { dialog, which ->
                //Yesが押された時の挙動
                finish()
            }
            .setNegativeButton("Cancel") { dialog, which ->
            // Noが押された時の挙動
            }
            .show()
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            //ボトムシートを上に浮き上がらせる
            val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.edit_bottom_sheet)
            val mBottomSheetBehavior = BottomSheetBehavior.from(view)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

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