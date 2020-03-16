package com.songspagetti.memoapplication

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

// LayoutContainer는 코틀린에서 제공하는 안드로이드용 Extension 패키지에 포함된 인터페이스로,
// 이 인터페이스를 상속받게 되면 컴파일 시에 캐시를 하는 코드를 자동으로 생성한다.
// containerView 라는 변수가 새로 생성자로 생기게 되고 이는 뷰를 캐시해두고 보여준다.
class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{


}





