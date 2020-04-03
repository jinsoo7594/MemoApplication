package com.songspagetti.memoapplication

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.songspagetti.memoapplication.data.MemoData
import io.realm.Realm
import java.util.*


// DetailActivity 에서 사용하는 ViewModel

class DetailVIewModel : ViewModel() {

    // 제목과 내용에 로드할 내용을 MutableLiveData 로 선언
    // 객체가 생성되자마자 value에 빈칸을 넣어준다.
    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val content: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    //알람시간을 가져와서 갱신하는 변수 추가
    val alarmTime: MutableLiveData<Date> = MutableLiveData<Date>().apply { value = Date(0) }

    private var memoData = MemoData()

    // ListViewModel 에서처럼 Raelm 인스턴스와 MemoDao 를 초기화하고 realm 을 닫아주는 코드 추가
    private val realm: Realm by lazy {
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
        memoData = memoDao.selectMemo(id)
        title.value = memoData.title
        content.value = memoData.content
        alarmTime.value = memoData.alarmTime
    }

    fun deleteAlarm() {
        alarmTime.value = Date(0) // 초기화
    }

    fun setAlarm(time: Date) { // 사용자가 입력한 알람시간 받아 갱신
        alarmTime.value = time
    }

    // 메모의 추가나 수정시 사용할 기능
    fun addOrUpdateMemo(context: Context, title: String, content: String) {
        // alarmTime 을 memoDao 에 넘겨주도록 수정
        val alarmTimeValue = alarmTime.value!!
        memoDao.addOrUpdateMemo(memoData, title, content, alarmTimeValue)

        // AlarmTool 을 통해 메모와 연결된 기존 알람정보를 삭제하고 새 알람시간이 현재시간 이후라면 새로 등록함
        AlarmTool.deleteAlarm(context, memoData.id)
        if (alarmTimeValue.after(Date())) {
            AlarmTool.addAlarm(context, memoData.id, alarmTimeValue)
        }
    }


}