package com.example.sharetn.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.Date.MainDate
import com.example.sharetn.Date.OriginTagDateClass
import com.example.sharetn.Date.TagDateClass
import com.example.sharetn.R
import io.realm.Realm
import java.util.*

class OriginTagRecyclerViewAdapter(private val context: Context,private val listener: OnItemClickListner):
    RecyclerView.Adapter<OriginTagRecyclerViewAdapter.ViewHolder>() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    //リサイクラービューに表示するリストを宣言する
    val items: MutableList<OriginTagDateClass> = mutableListOf()

    //データをcourseDateと結びつける？？
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val container: ConstraintLayout = view.findViewById(R.id.constraintEditTag)
        val itemEditTagText: EditText = view.findViewById(R.id.itemEditTagText)
        val brItemTop: View = view.findViewById(R.id.brItemTop)
        val brItemBottom:View = view.findViewById(R.id.brItemBottom)
        val dellEditButton: ImageButton = view.findViewById(R.id.dellEditButton)

    }

    //はめ込むものを指定
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_edittag_date_call,parent,false)
        return ViewHolder(view)
    }

    //itemsのposition番目の要素をviewに表示するコード
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemEditTagText.setText(item.name)

        holder.brItemTop.visibility = View.GONE
        holder.brItemBottom.visibility = View.GONE

//        EditTextをフォーカスしているか判定
        holder.itemEditTagText.setOnFocusChangeListener { view, hasFocus ->

//            フォーカスしているとき
            if (hasFocus) {
//                前後のBrを表示
                holder.brItemTop.visibility = View.VISIBLE
                holder.brItemBottom.visibility = View.VISIBLE
                holder.dellEditButton.setImageResource(R.drawable.delete_black_24dp)

//                消去するときに呼び出し元に1temを送る
                holder.dellEditButton.setOnClickListener {
                    listener.onItemClick(item)

                }


//                フォーカスしていないとき
            }else{
//                前後のBrの非表示
                holder.brItemTop.visibility = View.GONE
                holder.brItemBottom.visibility = View.GONE
                holder.dellEditButton.setImageResource(R.drawable.label_black_24dp)

//                  フォーカスが外れたらRealmに記入
                realm.executeTransaction{

                    val new = it.where(OriginTagDateClass::class.java).equalTo("Id",item.Id).findFirst()
                    new?.name = holder.itemEditTagText.text.toString()

                }
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
        fun onItemClick(item: OriginTagDateClass)
    }

    fun reView(){
        notifyDataSetChanged()
    }

    fun setList(list: List<OriginTagDateClass>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}