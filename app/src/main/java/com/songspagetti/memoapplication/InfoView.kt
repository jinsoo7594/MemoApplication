package com.songspagetti.memoapplication

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

// alarm notification
// 알람 + 위치 + 날씨정보에서 재사용 할 수 있도록 제작 예정

//생성자에 @JvmOverloads constructor 를 붙이는 이유는 안드로이드 시스템에서 View를 생성할 때 Java 생성자 형태로 호출하기 때문에 default arguments를 호환되도록 만들어 준다.(코틀린은 호환을 지원하지 않는다)
open class InfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) { // LinearLayout을 상속받아 생성자에 넘어온 값들을 넘겨준다.
    // init 내에서 View 의 inflate 함수를 사용하여 view_info.xml 을 내부에 포함시켜 View 로 동작한다.(사용한다)
    init {
        inflate(context, R.layout.view_info, this) // 해당 뷰를 이후 알람, 위치, 날씨정보에서 재사용할 예정
    }

}