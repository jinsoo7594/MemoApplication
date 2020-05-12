package com.songspagetti.memoapplication

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songspagetti.memoapplication.data.MemoData
import io.realm.Realm
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

//Realm 객체와 RealmResults는 기반 데이터에 의해 라이브로 자동 갱신되는 뷰입니다.
//즉, 결과를 매번 다시 가져올 필요가 없다는 의미입니다. 오브젝트를 수정하면 질의는 즉시 결과로 반영됩니다.

// Managed 관리객체 : Realm.createObject()를 호출하여 모든 필드가 기본 값으로 설정된 새로운 객체를 얻는다. 이 때 기본키가 기본값으로 설정되어 기존 객체와 충돌 할 수 있다.
// Unmanaged 비관리객체 : 이를 막기 위해 copyFromRealm()을 사용하여 비관리객체를 만들어 값을 설정한 후 원하는 시점에 copyToRealm() 을 통해 Realm으로 복사하여 관리 인스턴스로 생성하여 사용하는 것을 권장.

//Managed objects are persisted in Realm, are always up to date and thread confined. They are generally more lightweight than the unmanaged version as they take up less space on the Java heap.
//Unmanaged objects are just like ordinary Java objects, they are not persisted and they will not be updated automatically. They can be moved freely across threads.

//lifecycle이 STARTED or RESUMED state이면 observer가 active state라고 간주하고, LiveData는 active observers에게만 updates를 notifies한다.


// DetailActivity 에서 사용하는 ViewModel
class DetailViewModel : ViewModel() {

    // RealmObject 를 상속한 Realm 모델 객체
    var memoData = MemoData()
    //memoData 를 화면에 갱신해주는 memoLiveData
    val memoLiveData: MutableLiveData<MemoData> by lazy {
        MutableLiveData<MemoData>().apply { value = memoData }
    }


    // ListViewModel 에서처럼 Raelm 인스턴스와 MemoDao 를 초기화하고 realm 을 닫아주는 코드 추가
    private val realm: Realm by lazy {
        //getDefaultInstance 팩터리 메서드를 통해 이미 만들어진 realm 객체에 접근
        Realm.getDefaultInstance()
    }

    private val memoDao: MemoDao by lazy {
        MemoDao(realm)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    // 메모를 수정할 때 사용하기 위해 메모의 id를 받아 memoData 로드
    fun loadMemo(id: String) {
        memoData =
            realm.copyFromRealm(memoDao.selectMemo(id)) // realm.copyFromRealm()으로 unmanaged 객체로 만들어 원하는 시점에 DB에 저장할 수 있도록 함.
        memoLiveData.value = memoData // 받아온 unmanaged MemoData 객체를 momoLiveData 에도 할당해준다.
    }

    fun deleteAlarm() {
        memoData.alarmTime = Date(0)
        memoLiveData.value = memoData
    }

    fun setAlarm(time: Date) { // 사용자가 입력한 알람시간 받아 갱신
        memoData.alarmTime = time
        memoLiveData.value = memoData
    }

    fun deleteLocation() {
        memoData.latitude = 0.0
        memoData.longitude = 0.0
        memoLiveData.value = memoData
    }

    fun setLocation(latitude: Double, longitude: Double) {
        memoData.latitude = latitude
        memoData.longitude = longitude
        memoLiveData.value = memoData
    }

    // 메모의 추가나 수정시 사용할 기능
    fun addOrUpdateMemo(context: Context) {
        memoDao.addOrUpdateMemo(memoData)

        // AlarmTool 을 통해 메모와 연결된 기존 알람정보를 삭제하고 새 알람시간이 현재시간 이후라면 새로 등록함
        AlarmTool.deleteAlarm(context, memoData.id)
        if (memoData.alarmTime.after(Date())) {
            AlarmTool.addAlarm(context, memoData.id, memoData.alarmTime)
        }
    }

    fun deleteWeather() {
        memoData.weather = ""
        memoLiveData.value = memoData
    }

    fun setWeather(latitude: Double, longitude: Double) {
        // viewModelScope : ViewModel 이 소멸할 때 맞춰 스코프 내 실행되는 코루틴을 정지시켜 줌,  ViewModel kotlin extension 에서 제공
        viewModelScope.launch {
            memoData.weather = WeatherData.getCurrentWeather(latitude, longitude)
            memoLiveData.value = memoData
        }
    }

    //이미지를 받아 설정하는 함수(Context 도 받아야함)
    fun setImageFile(context: Context, bitmap: Bitmap) {
        //이미지를 저장할 파일 경로 생성(내부 저장소 앱 영역 image 폴더에 저장)
        val imageFile = File(
            // 앱 데이터 폴더 내에 image 라는 하위 폴더를 만들고 그 안에 메모id.jpg 라는 파일명으로 이미지 파일 생성
            context.getDir("image", Context.MODE_PRIVATE),
            memoData.id + ".jpg"
        )
        // 기존에 파일이 있는지 체크, 있으면 삭제
        if (imageFile.exists())
            imageFile.delete()

        try {
            //imageFile 객체에 지정된 경로로 새 파일 생성.
            imageFile.createNewFile()

            //FileOutputStream 파라미터로 받은 이미지 데이터를 JPEG 로 압축하여 저장하고 stream 객체 닫음.
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            // 저장이 끝나면 저장한 이미지 이름을 memoData 에 갱신함
            memoData.imageFile = memoData.id + ".jpg"
            memoLiveData.value = memoData

        } catch (e: Exception) {
            print(e)
        }
    }


}