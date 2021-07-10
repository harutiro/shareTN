package com.example.sharetn.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Date.MainDate
import com.example.sharetn.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.realm.Realm

class MainRecyclerViewAdapter(private val context: Context,private val listener: OnItemClickListner):
    RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    //リサイクラービューに表示するリストを宣言する
    val items: MutableList<MainDate> = mutableListOf()

    //データをcourseDateと結びつける？？
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
        val mainTextView: TextView = view.findViewById(R.id.mainTextView)
        val subTextView:TextView = view.findViewById(R.id.subTextView)
        val moreButton: ImageView =  view.findViewById(R.id.moreButton)
        val imageView:ImageView = view.findViewById(R.id.imageView)
        val container: ConstraintLayout = view.findViewById(R.id.constraint)
        val itemTagChipGroup: ChipGroup = view.findViewById(R.id.itemTagChipGroup)



    }

    //はめ込むものを指定
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_maindate_call,parent,false)
        return ViewHolder(view)
    }

    //itemsのposition番目の要素をviewに表示するコード
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // MainActivity側でタップしたときの動作を記述するため，n番目の要素を渡す
        holder.container.setOnClickListener { listener.onItemClick(item) }

//        itemとレイアウトの直接の結びつけ
        val decodedByte: ByteArray = Base64.decode(item.icon, 0)
        holder.iconImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size))
        holder.mainTextView.text = item.mainText
        holder.subTextView.text = item.subText
//        holder.tagListView
//        holder.moreButton

//        画像の結びつけ
        if(item.image != ""){
//            holder.imageView.setImageResource(item.image)
        }else{
//            holder.imageView.visibility = GONE
            holder.imageView.setImageResource(R.drawable.ramen2)
        }

        //chip関係
        //chipgroupの全消去
        holder.itemTagChipGroup.removeAllViews()

        for (index in item.tagList!!) {
            val chip = Chip(holder.itemTagChipGroup.context)
            chip.text= index.name

            // necessary to get single selection working
            chip.isClickable = true
            chip.isCheckable = true
            holder.itemTagChipGroup.addView(chip)
        }

//        Log.d("debag", holder.itemTagChipGroup.size.toString())

//        val courseDate: List <TagDateClass> = listOf(
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作dddddddddddddddddddddddddd者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//            TagDateClass(UUID.randomUUID().toString(),R.drawable.ic_baseline_more_vert_24 ,"作者"),
//
//
//        )
//
////        タグのリサイクラービューの部分の結びつけ
//        val adapter = TagRecyclerViewAdapter(context)
//        holder.tagRView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
//        holder.tagRView.adapter = adapter
//
//        //リサイクラービューアダプターで宣言したaddAllメソッドを呼んであげてデータも渡している
////        item.tagList?.let { adapter.addAll(it) }
//        adapter.addAll(courseDate)

    }

    //引数にとったリストをadapterに追加するメソッド
    fun addAll(items: List<MainDate>){
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    //リストの要素数を返すメソッド
    override fun getItemCount(): Int {

        return items.size
    }

    // RecyclerViewの要素をタップするためのもの
    interface OnItemClickListner{
        fun onItemClick(item: MainDate)
    }

    fun reView(){
        notifyDataSetChanged()
    }

    fun setList(list: List<MainDate>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}