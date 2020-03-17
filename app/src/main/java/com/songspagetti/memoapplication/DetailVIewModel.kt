package com.songspagetti.memoapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.songspagetti.memoapplication.data.MemoData
import io.realm.Realm


// DetailActivity 에서 사용하는 ViewModel

class DetailVIewModel : ViewModel() {

    // 제목과 내용에 로드할 내용을 MutableLiveData 로 선언
    // 객체가 생성되자마자 value에 빈칸을 넣어준다.
    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val content: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }

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
    }

    // 메모의 추가나 수정시 사용할 기능
    fun addOrUpdateMemo(title: String, content: String) {
        memoDao.addOrUpdateMemo(memoData, title, content)
    }


}