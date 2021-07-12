package com.example.sharetn.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

    }

    //はめ込むものを指定
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_edittag_date_call,parent,false)
        return ViewHolder(view)
    }

    //itemsのposition番目の要素をviewに表示するコード
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // MainActivity側でタップしたときの動作を記述するため，n番目の要素を渡す
        holder.container.setOnClickListener { listener.onItemClick(item) }

        holder.itemEditTagText.setText(item.name)

        holder.brItemTop.visibility = View.GONE
        holder.brItemBottom.visibility = View.GONE

        holder.itemEditTagText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                holder.brItemTop.visibility = View.VISIBLE
                holder.brItemBottom.visibility = View.VISIBLE


            }else{
                holder.brItemTop.visibility = View.GONE
                holder.brItemBottom.visibility = View.GONE

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