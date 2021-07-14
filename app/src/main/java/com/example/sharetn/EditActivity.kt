package com.example.sharetn

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.example.sharetn.dousa.UrlDomein
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


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

    var id:String? = ""

    var stateTagList: ArrayList<String>? = ArrayList<String>()


//    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)


        var comeText = ""
        var domein = ""
        var title = ""

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

        //URLのViewの非表示
        subEdit?.visibility = GONE
        subIcon?.visibility = GONE

        //スクロールできるように設定
        mainEdit?.movementMethod = ScrollingMovementMethod()
        subEdit?.movementMethod = ScrollingMovementMethod()
        memoEdit?.movementMethod = ScrollingMovementMethod()

        // MainActivityのRecyclerViewの要素をタップした場合はidが，fabをタップした場合は"空白"が入っているはず
        id = intent.getStringExtra("id")

//        ボトムシートを上に浮き上がらせる
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.edit_bottom_sheet)
        val mBottomSheetBehavior = BottomSheetBehavior.from(view)
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.detailsFAB).setOnClickListener{
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        findViewById<TextView>(R.id.tagSelecetTextView).setOnClickListener{
            val intent = Intent(this , SelectTagActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }

        //データのはめ込み
        if (id == null){

            val date = Date(System.currentTimeMillis())
            val df = SimpleDateFormat("yyyy年 MM月 dd日", Locale.JAPANESE)
            val formatted = df.format(date)
            dayText?.text = formatted

        }else{
            val item = realm.where(MainDate::class.java).equalTo("Id", id).findFirst()


            mainEdit?.setText(item?.mainText)
            subEdit?.setText(item?.subText)

            val decodedByte: ByteArray = Base64.decode(item?.icon, 0)
            mainIcon?.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size))

            dayText?.text = item?.dayText

            memoEdit?.setText(item?.memoText)

            print(item?.tagList)

            for (index in item?.tagList!!) {
                val new = realm.where(OriginTagDateClass::class.java).equalTo("Id",index.copyId).findFirst()


                val chip = Chip(editTagChipGroup?.context)
                chip.text= new?.name

                // necessary to get single selection working
                chip.isClickable = true
                chip.isCheckable = true
                editTagChipGroup?.addView(chip)
            }


            if(UrlDomein().check(item.subText)){
                subEdit?.visibility = VISIBLE
                subIcon?.visibility = VISIBLE
            }
        }





//＝＝＝＝＝＝＝＝＝＝＝＝＝＝Realm保存部分
        findViewById<TextView>(R.id.saveButton).setOnClickListener {
            save()
        }




//=============================共有で飛ばされたときに動く部分
        if (TextUtils.equals(intent.action, Intent.ACTION_SEND)) {
            val extras = intent.extras
            val extraText = extras!!.getCharSequence(Intent.EXTRA_TEXT).toString()

            subEdit?.visibility = VISIBLE
            subIcon?.visibility = VISIBLE

            //URLや文字の受け取り
            comeText = extraText
            domein = UrlDomein().hen(comeText)

            findViewById<EditText>(R.id.subEdit).setText(comeText)
            findViewById<EditText>(R.id.mainEdit).setText("NowLoading...")


            //URLで動く部分
            if(UrlDomein().check(comeText)) {

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
                webview.visibility = View.GONE

                //画像取得部分
                webview.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {

                        title = webview.title.toString()
                        mainEdit?.setText(title)


                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        url: String?
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
                it.where(MainDate::class.java).equalTo("Id",id).findFirst()
            }

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝BASE６４＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            //データ受け取り
            val bmp = (mainIcon?.drawable as BitmapDrawable).bitmap
            //エンコード
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            val output = Base64.encodeToString(b, Base64.NO_WRAP)

            new?.icon = output
            new?.mainText = mainEdit?.text.toString()
            new?.subText = subEdit?.text.toString()
            new?.memoText = memoEdit?.text.toString()
            new?.image = ""

            val date = Date(System.currentTimeMillis())
            val df = SimpleDateFormat("yyyy年 MM月 dd日", Locale.JAPANESE)
            val formatted = df.format(date)
            new?.dayText = formatted

            finish()
        }
    }

    //戻るボタンの処理
    override fun onBackPressed() {
        // 行いたい処理
        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
            .setTitle("注意")
            .setMessage("入力したデータを保存しないでホームに戻りますか？")
            .setPositiveButton("OK") { dialog, which ->
                //Yesが押された時の挙動
                finish()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Noが押された時の挙動
            }
            .setNeutralButton("保存") { dialog, which ->
                //その他が押された時の挙動
                save()
                finish()
            }
            .show()
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()

    }

    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 1000) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    stateTagList = data?.getStringArrayListExtra("stateTagList")
                }

                else -> {
                }
            }
        }

    }


}