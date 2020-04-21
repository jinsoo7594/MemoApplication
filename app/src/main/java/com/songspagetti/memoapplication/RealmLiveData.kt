package com.songspagetti.memoapplication

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

//Realm 객체와 RealmResults는 기반 데이터에 의해 라이브로 자동 갱신되는 뷰입니다.
//즉, 결과를 매번 다시 가져올 필요가 없다는 의미입니다. 오브젝트를 수정하면 질의는 즉시 결과로 반영됩니다.
//예를 들어 액티비티와 프래그먼트가 질의의 결과에 의존하고, 매 접근에 앞서 데이터를 최신으로 갱신할 필요 없이 Realm 객체나 RealmResults를 필드에 저장하여 쓸 수 있습니다.
// class ListViewModel
// val memoLiveData: RealmLiveData<MemoData> by lazy {
//     RealmLiveData<MemoData>(memoDao.getAllMemos())
// }
//  ㄴ RealmResults are always the result of a database query

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