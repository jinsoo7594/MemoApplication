package com.songspagetti.memoapplication

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

//LiveData 를 상속받아 class 를 만들고 생성자에서 RealmResults 를 받는다.
class RealmLiveData <T: RealmObject> (private val realmResults: RealmResults<T>)
    : LiveData<RealmResults<T>>(){

    // 받아온 realmResuts 를 value 에 추가 -> Observe 가 동작하도록 하기 위해
    init{
        value = realmResults
    }
    //RealmResult 가 갱신될 때 동작할 리스너 작성 ( 갱신되는 값을 value 에 할당)
    private val listener = RealmChangeListener<RealmResults<T>>{value = it}

    //LiveData 가 활성화 될 때 realmResults 에 리스너를 붙여준다.
    override fun onActive() {
        super.onActive()
        realmResults.addChangeListener(listener)
    }

    //LiveData 가 비활성화 될 때 리스너를 제거한다.
    override fun onInactive(){
        super.onInactive()
        realmResults.removeChangeListener(listener)
    }



}