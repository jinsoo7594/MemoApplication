package com.songspagetti.memoapplication

import com.songspagetti.memoapplication.data.MemoData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

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
    fun addOrUpdateMemo(memoData: MemoData, title: String, content: String){
        // ** DB 를 업데이트하는 쿼리는 반드시 executeTransaction() 함수로 감싸야 한다.
        // 다른 곳에서 DB를 수정할 수 없도록 요청들을 대기시켜 쿼리가 끝날 때까지 DB를 안전하게 사용 가능하게 해준다.
        realm.executeTransaction{
            memoData.title = title
            memoData.content = content
            memoData.createdAt = Date()

            if(content.length > 100)
                memoData.summary = content.substring(0..100)
            else
                memoData.summary = content
            // 여기까지 수정문
            // Managed 상태가 아닌경우 copyToRealm()함수로 DB에 추가
            if(!memoData.isManaged){
                    it.copyToRealm(memoData)
                }
        }
    }
}