package com.songspagetti.memoapplication

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import io.realm.Realm

// 앱을 제어하는 객체인 Application 을 상속받아 클래스 생성
class SongMemoApplication () : Application(){
   // 앱 시작시 실행되는 onCreat() 함수를 override
    override fun onCreate() {
        super.onCreate()
       //Realm DB 초기화
        Realm.init(this)
       NaverMapSdk.getInstance(this).setClient(
           NaverMapSdk.NaverCloudPlatformClient("4qyisqtupf")
       )
    }
}
// 이 코드를 앱에 연동하려면 manifest 로 이동하여 <application 태그의 name 속성에 ".SongMemoApplication" 연결