package com.songspagetti.memoapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    // ViewModel 을 담을 변수  --> View 인 Activity 는 ViewModel 의 참조 값을 갖는다.
    private var viewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
        //fab를 누르면 DetailActivity로 이동
        fab.setOnClickListener { view ->
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
            /*
            viewModel!!.let{
                var memoData = MemoData()
                memoData.title = "제목 테스트"
                memoData.summary = "요약내용 테스트"
                memoData.createdAt = Date()
                it.addMemo(memoData)
            }*/

        }
        // MemoListFragment 를 화면에 표시
        val fragmentTransaction = supportFragmentManager.beginTransaction().replace(R.id.contentLayout, MemoListFragment()).commit()

        // ListViewModel 을 가져오는 코드, 앱의 객체인 application 이 null 인지 먼저 체크함
        viewModel = application!!.let{
            // ViewModel 을 가져오기위해 ViewModelProvider 객체 생성
            // viewModelStore 은 ViewModel의 생성과 소멸의 기준, ViewModelFactory 는 ViewModel을 실제로 생성하는 객체
            // ViewModelProvider의 get함수를 통해 ListViewModel을 얻을 수 있음
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it)).get(ListViewModel::class.java)

        }
    }

}
