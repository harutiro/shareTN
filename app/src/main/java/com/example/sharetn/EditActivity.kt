package com.example.sharetn

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Adapter.MainRecyclerViewAdapter
import com.example.sharetn.Adapter.TagRecyclerViewAdapter
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.TagDateClass
import com.example.sharetn.dousa.UrlDomein
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults
import java.util.*


class EditActivity : AppCompatActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //realmのインスタンス
        val realm: Realm = Realm.getDefaultInstance()

        var comeText = ""
        var domein = ""
        var title = ""

        //URLのViewの非表示
        findViewById<EditText>(R.id.subEdit).visibility = GONE
        findViewById<ImageView>(R.id.subIcon).visibility = GONE

        //スクロールできるように設定
        findViewById<TextView>(R.id.mainEdit).movementMethod = ScrollingMovementMethod()
        findViewById<TextView>(R.id.subEdit).movementMethod = ScrollingMovementMethod()
        findViewById<TextView>(R.id.memoEdit).movementMethod = ScrollingMovementMethod()

        //タグ関係
        val courseDate: List <TagDateClass> = listOf(
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作dddddddddddddddddddddddddd者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),


            )

        val tagPersons: RealmResults<TagDateClass> = realm.where(TagDateClass::class.java).findAll()
        val RView = findViewById<RecyclerView>(R.id.EditRView)
        val adapter = TagRecyclerViewAdapter(this )
        RView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        RView.adapter = adapter

        adapter.addAll(courseDate)




//=============================共有で飛ばされたときに動く部分
        if (TextUtils.equals(intent.action, Intent.ACTION_SEND)) {
            val extras = intent.extras
            val extraText = extras!!.getCharSequence(Intent.EXTRA_TEXT).toString()

            findViewById<EditText>(R.id.subEdit).visibility = VISIBLE
            findViewById<ImageView>(R.id.subIcon).visibility = VISIBLE

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

                wm.addView(webview, params)

                //非表示
                webview.visibility = View.GONE

                //画像取得部分
                webview.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {

                        title = webview.title.toString()
                        findViewById<EditText>(R.id.mainEdit).setText(title)


                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        url: String?
                    ){
                        findViewById<EditText>(R.id.mainEdit).setText("ネットワークエラー")
                    }

                }
            }
        }






    }
}