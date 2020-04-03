package com.songspagetti.memoapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.songspagetti.memoapplication.data.MemoData
import kotlinx.android.synthetic.main.item_memo.view.*
import java.text.SimpleDateFormat
// 리스트를 표시하기 위해 Memo를 담은 리스트를 생성자로 받는다.
class MemoListAdapter (private val list: MutableList<MemoData>): RecyclerView.Adapter<ItemViewHolder>(){
    //Date객체를 사람이 볼수있는 문자열로 변환
    private val dateFormat = SimpleDateFormat("MM/dd HH:mm")

    lateinit var itemClickListener: (itemId: String) -> Unit // >>>???


    //item_memo를 불러 ViewHolder 생성, 리사이클러뷰는 뷰를 알아서 붙여주므로 false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)

        // 아이템이 클릭될 때 view 의 tag 에서 메모id 를 받아서 리스너에 넘김
        view.setOnClickListener {
            itemClickListener.run {
                val memoId = it.tag as String
                this(memoId) // item_memo 아이템을 클릭할 때마다 "Function1<java.lang.String, kotlin.Unit>" 가 itemClickListener에 저장된다.

            }
        }
        return ItemViewHolder(view)
    }
    // list 내의 MemoData 의 개수 반환
    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // 제목이 있는 경우 titleView 를 화면에 표시(VISIBLE)하고 title 값을 할당하여 보여준다.
        if(list[position].title.isNotEmpty()){
            holder.containerView.titleView.visibility = View.VISIBLE
            holder.containerView.titleView.text = list[position].title
        }
        //VISIBLE : View를 화면에 표시, INVISIBLE : View의 내용만 감추고 영역은 유지, GONE : View의 내용 및 영역까지 제
        else{
            holder.containerView.titleView.visibility = View.GONE
        }
       holder.containerView.summaryView.text = list[position].summary
        //holder.summaryView.text = list[position].summary
        //내부적으로는 id로 findViewById로 생성한 View 변수를 참조하느냐 LayoutContainer를 이용하여 containerView에 캐시된 View 변수를 참조하느냐의 차이
        holder.containerView.dateView.text = dateFormat.format(list[position].createdAt)
        holder.containerView.tag = list[position].id

    }



}