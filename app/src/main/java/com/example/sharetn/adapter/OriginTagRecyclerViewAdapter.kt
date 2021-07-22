package com.example.sharetn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.sharetn.R
import com.example.sharetn.date.OriginTagDateClass
import io.realm.Realm

class OriginTagRecyclerViewAdapter(private val context: Context,private val listener: OnItemClickListner):
    RecyclerView.Adapter<OriginTagRecyclerViewAdapter.ViewHolder>() {

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    //リサイクラービューに表示するリストを宣言する
    val items: MutableList<OriginTagDateClass> = mutableListOf()

    //データをcourseDateと結びつける？？
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val itemEditTagText: EditText = view.findViewById(R.id.itemEditTagText)
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

//        EditTextをフォーカスしているか判定
        holder.itemEditTagText.setOnFocusChangeListener { _, hasFocus ->

//            フォーカスしているとき
            if (hasFocus) {
                holder.dellEditButton.setImageResource(R.drawable.delete_black_24dp__1_)

//                消去するときに呼び出し元に1temを送る
                holder.dellEditButton.setOnClickListener {
                    listener.onItemClick(item)

                }


//                フォーカスしていないとき
            }else{
                holder.dellEditButton.setImageResource(R.drawable.label_black_24dp)

                holder.dellEditButton.setOnClickListener {

                }

//                  フォーカスが外れたらRealmに記入
                realm.executeTransaction{

                    val new = it.where(OriginTagDateClass::class.java).equalTo("id",item.id).findFirst()
                    new?.name = holder.itemEditTagText.text.toString()

                }
            }
        }






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