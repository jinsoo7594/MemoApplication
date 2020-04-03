package com.songspagetti.memoapplication

import android.content.Context
import android.util.AttributeSet
import kotlinx.android.synthetic.main.view_info.view.*
import java.text.SimpleDateFormat
import java.util.*

// 생성자는 InfoView.kt 와 동
class AlarmInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InfoView(context, attrs, defStyleAttr) { // InfoView 를 상속받음
    //클래스 공용부인 companion object 안에 알람의 시간표시형식을 설정
    companion object {
        private val dateFormat = SimpleDateFormat("yy/MM/dd HH:mm")
    }

    // View 에 표시할 초기값 지정
    init {
        typeImage.setImageResource(R.drawable.ic_alarm)
        infoText.setText("")
    }

    fun setAlarmdate(alarmDate: Date) {
        //알람시간이 현재 시간보다 이전이면
        if (alarmDate.before(Date())) {
            infoText.setText("알람이 없습니다")
        }
        //알람시간이 현재시간보다 이후라면 형식에 맞춰 알람시간 표시
        else {
            infoText.setText(dateFormat.format(alarmDate))
        }
    }
}