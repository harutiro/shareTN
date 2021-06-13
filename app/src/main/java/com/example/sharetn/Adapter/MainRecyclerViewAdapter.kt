package com.example.sharetn.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Date.MainDate
import com.example.sharetn.R

class MainRecyclerViewAdapter(private val context: Context):
    RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>() {

    //リサイクラービューに表示するリストを宣言する
    val items: MutableList<MainDate> = mutableListOf()

    //データをcourseDateと結びつける？？
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
        val mainTextView: TextView = view.findViewById(R.id.mainTextView)
        val subTextView:TextView = view.findViewById(R.id.subTextView)
        val tagListView: ListView = view.findViewById(R.id.tagListView)
        val moreButton: ImageView =  view.findViewById(R.id.moreButton)
        val imageView:ImageView = view.findViewById(R.id.imageView)



    }

    //はめ込むものを指定
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_maindate_call,parent,false)
        return ViewHolder(view)
    }

    //itemsのposition番目の要素をviewに表示するコード
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.iconImageView.setImageResource(item.icon)
        holder.mainTextView.text = item.mainText
        holder.subTextView.text = item.subText
//        holder.tagListView
//        holder.moreButton

        if(item.image != ""){
//            holder.imageView.setImageResource(item.image)
        }else{
            holder.imageView.visibility = GONE
        }
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
}