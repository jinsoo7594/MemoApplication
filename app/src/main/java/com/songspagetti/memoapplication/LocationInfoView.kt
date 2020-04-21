package com.songspagetti.memoapplication

import android.content.Context
import android.location.Geocoder
import android.util.AttributeSet
import kotlinx.android.synthetic.main.view_info.view.*
import java.util.*

class LocationInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InfoView(context, attrs, defStyleAttr) {
    init {
        typeImage.setImageResource(R.drawable.ic_location)
        infoText.setText("") // 택스트 비워둠.

    }

    fun setLocation(latitude: Double, longitude: Double) {
        if (latitude == 0.0 && longitude == 0.0) {
            infoText.setText("위치정보가 없습니다.")

        } else {
            // 위치값이 있다면 구글에서 제공하는 Geocoder로 좌표를 지역 이름으로 변환하여 출력
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(latitude, longitude, 1)
            infoText.setText("${address[0].adminArea}")
        }
    }

}