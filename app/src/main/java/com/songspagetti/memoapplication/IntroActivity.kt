package com.songspagetti.memoapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
// 일정한 시간이 지난 후 ListActivity로 이동
class IntroActivity : AppCompatActivity() {

    var handler: Handler? = null // Handler : Runnable을 실행하는 클래스
    var runnable: Runnable? = null // Runnable : 병렬실행이 가능한 Thread를 만들어주는 클래스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        //안드로이드 앱을 띄우는 Window의 속성을 변경하여 시스템UI를 숨기고 전체화면으로 표시하는 코드
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION


    }

    override fun onResume() {
        super.onResume()
        //Runnable이 실행되면 ListActivity로 이동
        runnable = Runnable{
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }
        // Handler를 생성하고 2초뒤에 Runnable 실행
        handler = Handler()
        handler?.run{
            postDelayed(runnable, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        // Activity Pause 상태일 때는 runnable도 중단
        handler?.removeCallbacks(runnable)
    }
}
