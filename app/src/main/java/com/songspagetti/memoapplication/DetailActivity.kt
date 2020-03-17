package com.songspagetti.memoapplication

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private var viewModel: DetailVIewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        // viewModel 생성
        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(DetailVIewModel::class.java)
        }
        // 제목과 내용에 Observer 를 걸어 화면을 갱신
        viewModel!!.let {
            it.title.observe(this, Observer { supportActionBar?.title = it })
            it.content.observe(this, Observer { contentEdit.setText(it) })
        }
        // ListActivity 에서 특정 아이템(메모)을 선택했을 때 보내주는 메모 id를 받아 데이터를 로드함
        // 새로 작성하는 메모일 경우 메모id 가 없어 이 루틴은 동작하지 않는다.
        val memoId = intent.getStringExtra("MEMO_ID")
        if (memoId != null) viewModel!!.loadMemo(memoId)

        toolbar_layout.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null)
            val titleEdit = view.findViewById<EditText>(R.id.titleEdit)

            // 툴바 레이아웃을 눌렀을때 제목을 수정하는 다이얼로그 띄움
            AlertDialog.Builder(this)
                .setTitle("제목을 입력하세요")
                .setView(view)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    supportActionBar?.title = titleEdit.text.toString() // 확인 버튼을 누르면 제목 변경
                }).show()

        }
    }

    // 뒤로가기 눌렀을 때 동작
    override fun onBackPressed() {
        super.onBackPressed()
        // 메모 DB 갱신
        viewModel?.addOrUpdateMemo(
            supportActionBar?.title.toString(),
            contentEdit.text.toString()
        )
    }
}
