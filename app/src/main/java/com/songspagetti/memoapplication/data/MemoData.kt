package com.songspagetti.memoapplication.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

// class를 open class로 바꾸고 RealmObject 상속받음
open class MemoData (
    @PrimaryKey  // 테이블에서 레코드를 구분할 수 있는 고유값으로 id 위에 기입
    var id: String = UUID.randomUUID().toString(), //메모의 고유 ID, UUID : 랜덤한 고유값 자동 생
    var createdAt: Date = Date(),
    var title: String = "",
    var content: String = "",
    var summary: String = "",
    var imageFile: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var alarmTime: Date = Date(),
    var weather: String = ""
) : RealmObject()
//Realm 모델 객체는 특정 객체를 상속받거나 구현해야 합니다. 즉, 상속은 RealmObject를 상속하거나, RealmModel을 구현해야 합니다.
// 이렇게 만든 모델 객체는 Realm에 관리되는 managed, 혹은 일종의 POJO 객체인 unmanaged 두 가지 상태 중 하나를 가집니다.
