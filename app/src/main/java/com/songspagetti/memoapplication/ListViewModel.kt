package com.songspagetti.memoapplication

import androidx.lifecycle.ViewModel

//LiveData의 data가 변경될때 LifecycleOwner가 활성화 되어있는한 등록된 모든 옵저버들에게 이벤트를 보내게 된다.
//LiveData는 UI컨트롤러 옵저버들이 데이터 변화를 감지하고 알맞게 UI를 자동으로 변경할 수 있도록 해준다.
// MutableLiveData : postValue(in Worker Thread), setValue(in MainThread) 메서드를 이용해서 값 변경 가능
// LiveData : abstract class 이기 때문에 이를 상속받은 MutableLiveData 를 사용한다.
class ListViewModel : ViewModel(){
/*
    private val memos: MutableList<MemoData> = mutableListOf() //MemoData의 MutableList를 저장하는 속성 선언
    // 위에 선언한 MutableList를 담을 MutableLiveData를 추가
    // 성능을 위해 lazy를 사용하여 지연 초기화
    // UI에서 DB, 리스트등 대량의 데이터를 다루는 경우 lazy 내에서 초기값을 할당하는 것이 좋다.
    val memoLiveData: MutableLiveData<MutableList<MemoData>> by lazy{
        MutableLiveData<MutableList<MemoData>>().apply{
            value = memos
        }
    }
    // 메모(MemoData 객체)를 리스트에 추가하고
    // MutableLiveData의 value를 갱신하여 Observer를 호출하도록 하는 함수
    fun addMemo(data: MemoData){
        val tempList = memoLiveData.value // 기존의 value, 즉 mutablelist를 가져와서
        tempList?.add(data) //메모 데이터를 추가한 뒤
        memoLiveData.value = tempList // 다시 value에 넣어줘야 한다.

    }*/


}