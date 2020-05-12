package com.songspagetti.memoapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// 일정한 시간이 지난 후 ListActivity로 이동
class IntroActivity : AppCompatActivity() {

    var handler: Handler? = null // Handler : Runnable을 실행하는 클래스
    var runnable: Runnable? = null // Runnable : 병렬실행이 가능한 Thread를 만들어주는 클래스

    companion object {
        // 권한 요청시 권한 Activity에 전달할 고유코드(상수) 추가
        private const val REQUEST_LOCATION_PERMISSION_CODE = 100
    }

    // Manifest 에 추가한 위치정보에 대한 두 권한을 사용자가 허가했는지 체크하는 함수
    fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission && coarseLocationPermission
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        //안드로이드 앱을 띄우는 Window의 속성을 변경하여 시스템UI를 숨기고 전체화면으로 표시하는 코드
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION


    }

    fun moveListActivity() {
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

    override fun onResume() {
        super.onResume()

        if (checkLocationPermission()) {
            moveListActivity()
        } else {
            //shouldShowRequestPermissionRationale 함수로 사용자가 권한을 거절했던 적이 있는지 확인하고 안내메시지 출
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(this, "이 앱을 실행하려면 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
            //앱에 필요한 권한을 사용자에게 요청하는 시스템 Activity 띄움
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_LOCATION_PERMISSION_CODE
            )
        }

    }

    override fun onPause() {
        super.onPause()
        // Activity Pause 상태일 때는 runnable도 중단
        handler?.removeCallbacks(runnable)
    }
}
