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
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String as String1


class EditActivity : AppCompatActivity() {

    private val requestCode = 1000

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

    var archive :Boolean = false

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        title = "編集"

        subEdit = findViewById(R.id.subEdit)
        subIcon = findViewById(R.id.subIcon)
        mainEdit = findViewById(R.id.mainEdit)
        mainIcon = findViewById(R.id.mainIcon)
        dayText = findViewById(R.id.dayText)
        dayIcon = findViewById(R.id.dayIcon)
        memoIcon = findViewById(R.id.memoIcon)
        memoEdit = findViewById(R.id.memoEdit)
        image = findViewById(R.id.image)
        editTagChipGroup = findViewById(R.id.editTagChipGroup)

        //スクロールできるように設定
        mainEdit?.movementMethod = ScrollingMovementMethod()
        subEdit?.movementMethod = ScrollingMovementMethod()
        memoEdit?.movementMethod = ScrollingMovementMethod()

        // MainActivityのRecyclerViewの要素をタップした場合はidが，fabをタップした場合は"空白"が入っているはず
        id = intent.getStringExtra("id")
        stateEditMode = intent.getBooleanExtra("editMode",false)

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
            archive = item?.archive!!

            //＝＝＝＝＝＝＝＝＝＝＝＝タグのはめ込み部分＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            for(i in item.tagList!!){
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
            if (mainEdit?.text.toString().isBlank()) {
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"タイトルが入力されていません。", Snackbar.LENGTH_SHORT)
                snackbar.view.setBackgroundResource(R.color.error)
                snackbar.show()
            }else{
                save()
            }
        }

//    ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝消去部分
        findViewById<ImageButton>(R.id.removeButton2).setOnClickListener{
            val person = realm.where(MainDate::class.java).equalTo("id",id).findFirst()

            realm.executeTransaction {
                person?.deleteFromRealm()
            }

            finish()

        }

        findViewById<ImageButton>(R.id.shareButton).setOnClickListener{
            var putText = "【タイトル】\n"
            putText += mainEdit?.text.toString() + "\n"
            putText += "【URL】\n"
            putText += subEdit?.text.toString() + "\n"
            putText += "【内容】\n"
            putText += memoEdit?.text.toString()

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,putText )
            }
            startActivity(intent)
        }




//=============================共有で飛ばされたときに動く部分
        if (TextUtils.equals(intent.action, Intent.ACTION_SEND)) {
            val extras = intent.extras
            val comeText = extras!!.getCharSequence(Intent.EXTRA_TEXT).toString()

            findViewById<EditText>(R.id.mainEdit).setText(comeText)

            //URLで動く部分
            if(Regex("http://").containsMatchIn(comeText) || Regex("https://").containsMatchIn(comeText)) {

                //URLや文字の受け取り
                val domein: String1 = comeText.removePrefix("https://").removePrefix("http://").split("/")[0]

                findViewById<EditText>(R.id.subEdit).setText(comeText)
                findViewById<EditText>(R.id.mainEdit).setText("NowLoading...")

                subEdit?.visibility = VISIBLE
                subIcon?.visibility = VISIBLE

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
            new?.archive = archive

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
            .setPositiveButton("OK") { _, _ ->
                //Yesが押された時の挙動
                finish()
            }
            .setNegativeButton("Cancel") { _, _ ->
            // Noが押された時の挙動
            }
            .show()
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
//            val view = findViewById<ConstraintLayout>(R.id.edit_bottom_sheet)
//            val mBottomSheetBehavior = BottomSheetBehavior.from(view)
//            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            true
        }

        R.id.archive_settings -> {

            archive = !archive

            if(archive){
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
        inflater.inflate(R.menu.edit_activity_menu, menu)

        val menuArchive = menu?.findItem(R.id.archive_settings)

        if(archive){
            menuArchive?.setIcon(R.drawable.unarchive_black_24dp)
        }else{
            menuArchive?.setIcon(R.drawable.archive_black_24dp)
        }


        return super.onCreateOptionsMenu(menu)
    }


}