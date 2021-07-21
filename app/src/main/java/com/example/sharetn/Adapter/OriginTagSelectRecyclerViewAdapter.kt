package com.example.sharetn.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Date.OriginTagDateClass
import com.example.sharetn.R
import io.realm.Realm

class OriginTagSelectRecyclerViewAdapter(private val context: Context, private val listener: OnItemClickListner):
    RecyclerView.Adapter<OriginTagSelectRecyclerViewAdapter.ViewHolder>(){

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    //リサイクラービューに表示するリストを宣言する
    val items: MutableList<OriginTagDateClass> = mutableListOf()

    val states: MutableList<String> = mutableListOf()

    //データをcourseDateと結びつける？？
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tagTextView: TextView = view.findViewById(R.id.tagTextView)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val constraintSelectTag:ConstraintLayout = view.findViewById(R.id.constraintSelectTag)

    }

    //はめ込むものを指定
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_edittag_select_date_call,parent,false)
        return ViewHolder(view)
    }

    //itemsのposition番目の要素をviewに表示するコード
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tagTextView.text = item.name

        holder.checkBox.setOnClickListener{
            listener.onItemClick(item,holder.checkBox.isChecked)
        }

        holder.constraintSelectTag.setOnClickListener{
            holder.checkBox.isChecked = !holder.checkBox.isChecked

            listener.onItemClick(item,holder.checkBox.isChecked)
        }

        for( i in states){
            if( i == item.id){
                holder.checkBox.isChecked = true
            }
        }
    }

    //引数にとったリストをadapterに追加するメソッド
    fun addAll(items: List<OriginTagDateClass>){
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    //リストの要素数を返すメソッド
    override fun getItemCount(): Int {

        return items.size
    }

    // RecyclerViewの要素をタップするためのもの
    interface OnItemClickListner{
        fun onItemClick(item: OriginTagDateClass,state:Boolean)
    }

    fun reView(){
        notifyDataSetChanged()
    }

    fun setList(list: List<OriginTagDateClass>,stateList:List<String>){
        items.clear()
        items.addAll(list)
        states.clear()
        states.addAll(stateList)

        notifyDataSetChanged()
    }
}