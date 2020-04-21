package com.songspagetti.memoapplication


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_memo_list.*

/**
 * A simple [Fragment] subclass.
 */
class MemoListFragment : Fragment() {

    private lateinit var listAdapter: MemoListAdapter
    private var viewModel: ListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memo_list, container, false)
    }
    // Fragment LifeCycle
    // onAttach() : Fragment 를 Activity 에 attach 할 때 ->onCreate() : 초기화 리소스 작업 ->onCreateView() : Layout 을 inflate 하는곳, View 객체를 얻어 초기화
    // -> onActivityCreated() : Fragment 생성 이후->onStart() : Fragment 화면에 표시될 때 ->onResume() : Fragment 화면에 로딩 끝났을 때
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // viewmodel을 가져온다. 여기서 activity 속성은 ListActivity 를 가리킨다.

        viewModel = activity!!.application!!.let{
            ViewModelProvider(
                activity!!.viewModelStore, ViewModelProvider.AndroidViewModelFactory(it)).get(ListViewModel::class.java)
        }
        // --- ListActivity 에서 viewModel 을 가져오는 코드와의 비교 ---
        //viewModel = application!!.let{
        //            ViewModelProvider(
        //                viewModelStore, ViewModelProvider.AndroidViewModelFactory(it)).get(ListViewModel::class.java)
        //             }
        // Fragment 역시 자체적인 viewModelStore 을 가지고 있지만, activity 의 viewModelStore 속성을 사용하는 이유는
        // activity 와 viewModel 을 공유하기 위해서이다. 만약 자체 viewModelStore 을 사용한다면 MemoListFragment 만의 viewModel 이 따로 생성되어 데이터가 공유되지 않는다.

        // 생성된 viewModel 에서 MemoLiveData 를 가져와서 Adapter 에 담아 RecyclerView 에 출력하도록 한다.
        viewModel!!.let{
            it.memoLiveData.value?.let{
                listAdapter = MemoListAdapter(it)
                memoListView.adapter = listAdapter
                memoListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                listAdapter.itemClickListener = {
                    val intent = Intent(activity, DetailActivity::class.java)
                    intent.putExtra("MEMO_ID", it)
                    startActivity(intent) 
                }
            }
            // LiveData 개체인 MemoLiveData 에 observe 함수를 통해 값이 변할 때 동작할 Observer 를 붙여줌 (Observer 내에서는 adapter 의 갱신코드 호출)
            // void observe(LifecycleOwner owner, Observe<T> observer) /// LiveData 클래스 내 메소드
            // Adds the given observer to the observers list within the lifespan of the given owner.
            it.memoLiveData.observe(this,
                Observer{
                    listAdapter.notifyDataSetChanged()
                }
            )


        }

    }

    // 메모를 작성하고 되돌아 왔을 때 리스트가 갱신되도록 한다.
    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }


}
