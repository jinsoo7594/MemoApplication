package com.songspagetti.memoapplication

import com.songspagetti.memoapplication.data.MemoData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

//Realm 객체와 RealmResults는 기반 데이터에 의해 라이브로 자동 갱신되는 뷰입니다.
//즉, 결과를 매번 다시 가져올 필요가 없다는 의미입니다. 오브젝트를 수정하면 질의는 즉시 결과로 반영됩니다.
//예를 들어 액티비티와 프래그먼트가 질의의 결과에 의존하고, 매 접근에 앞서 데이터를 최신으로 갱신할 필요 없이 Realm 객체나 RealmResults를 필드에 저장하여 쓸 수 있습니다.

//DAO (Data Access Object) : DB 에 직접 접근하지 않고 필요한 쿼리를 함수로 미리 작성하여 쿼리의 재사용성을 높이는 것
// 생성자로 Realm 인스턴스를 받는 클래스
class MemoDao (private val realm: Realm){
    // DB 에 담긴 MemoData 를 생성시간의 역순으로 정렬하여 받아 온다.
    fun getAllMemos(): RealmResults<MemoData>{
        return realm.where(MemoData::class.java) // 쿼리할 대상이 되는 RealmObject 지정(내부적으로는 테이블)
            .sort("createdAt", Sort.DESCENDING) //equalTo(),and(),notEqualTo() 등의 쿼리에 필요한 검색조건(조건이 없는경우 모든 값)
            .findAll() // 쿼리의 모든 결과값을 받아오는 함수(RealmResults 자료형), count() 쿼리된 결과의 개수(Long)
                       // findFirst(), findLast() : 쿼리의 첫번째/마지막 결과값만(RealmObject)
    }// 여기서 findAll() 로 반환되는 RealmResults 자료형은 인덱스로 참조 가능, 반복문에서 사용 가능, 리스트 변수와 호환이 가능하다.

    fun selectMemo(id: String): MemoData{
        return realm.where(MemoData::class.java)
            .equalTo("id", id)
            .findFirst() as MemoData
    }
    // 메모 생성 or 수정
    fun addOrUpdateMemo(memoData: MemoData) {
        // ** DB 를 업데이트하는 쿼리는 반드시 executeTransaction() 함수로 감싸야 한다.
        // 다른 곳에서 DB를 수정할 수 없도록 요청들을 대기시켜 쿼리가 끝날 때까지 DB를 안전하게 사용 가능하게 해준다.
        realm.executeTransaction{

            memoData.createdAt = Date()


            if (memoData.content.length > 100)
                memoData.summary = memoData.content.substring(0..100)
            else
                memoData.summary = memoData.content
            // 여기까지 수정문
            // Managed 상태가 아닌경우 copyToRealm()함수로 DB에 추가
            if(!memoData.isManaged){
                    it.copyToRealm(memoData)
                }
        }
    }

    //전체 MemoData 중 alarmTime 이 현재시간(Date())보다 큰 데이터만 가져오는 함수
    fun getActiveAlarms(): RealmResults<MemoData> {
        return realm.where(MemoData::class.java)
            .greaterThan("alarmTime", Date())
            .findAll()
    }


}