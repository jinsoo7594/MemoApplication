package com.songspagetti.memoapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.takisoft.datetimepicker.DatePickerDialog
import com.takisoft.datetimepicker.TimePickerDialog
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.io.File
import java.util.*

class DetailActivity : AppCompatActivity() {

    private var viewModel: DetailViewModel? = null
    private val dialogCalendar =
        Calendar.getInstance() // 날짜와 시간 다이얼로그에서 설정 중인 값을 임시로 저장해 두기 위한 Calendar 변수 추가

    // 기기내 저장된 이미지는 Intent를 통해 불러올 수 있다.
    // Intent 로 Activity 결과를 요청할 때 사용하는 요청 코드 값 추가
    private val REQUEST_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        // 아직 미정인 기능
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //    .setAction("Action", null).show()

            // 기기내에서 이미지 파일을 읽어올 수 있는 ACTION_GET_CONTENT 를 이용하여 해당 기능이 있는 activity를 호출함
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }
        // viewModel 생성
        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(DetailViewModel::class.java)
        }
        // viewModel의 memoLiveData를 observe 하도록 함. 즉 memoLiveData가 참조하는 Realm Object 객체인 memoData 에 변화가 생기면 실행.
        viewModel!!.memoLiveData.observe(this, Observer {
            supportActionBar?.title = it.title
            contentEdit.setText(it.content)
            alarmInfoView.setAlarmdate(it.alarmTime)
            locationInfoView.setLocation(it.latitude, it.longitude)
            weatherInfoView.setWeather(it.weather)

            // 이미지 파일 경로를 Uri 로 바꾸어 bgImage (이미지뷰) 에 설정함
            val imageFile = File(
                getDir("image", Context.MODE_PRIVATE),
                it.id + ".jpg"
            )

            bgImage.setImageURI(imageFile.toUri())


        })
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
                    viewModel!!.memoData.title =
                        titleEdit.text.toString() // 제목이 변경될 때 memoData도 함께 갱신. 이때 연쇄적으로 memoData.title이 변경된걸 옵저버가 감지하여 정해진 일을 수행한다.
                }).show()

        }
        // 내용이 변경될 때마다 Listener 내에서 viewModel의 memoData의 내용도 같이 변경해 줌.
        contentEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel!!.memoData.content = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        locationInfoView.setOnClickListener {
            val latitude = viewModel!!.memoData.latitude
            val longitude = viewModel!!.memoData.longitude

            if (!(latitude === 0.0 && longitude == 0.0)) { // 좌표 유효 여부 확
                val mapView = MapView(this) // naver sdk 에서 제공하는 MapView 객체 생성(지도를 출력하는 View)
                // 네이버 지도의 옵션을 변경하기 위해선 맵이 로드된 후에 반환되는 NaverMap 객체가 필요한데.
                // NaverMap 객체를 받기 위해서 getMapAsync() 함수에 이벤트 리스너를 지정하여 받을 수 있다.
                // 위치정보를 NaverMap의 CameraUpdate 객체에 담아 현재 지도에 카메라 위치를 적용해 준다.
                mapView.getMapAsync {
                    val latitude = viewModel!!.memoData.latitude
                    val longitude = viewModel!!.memoData.longitude
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                    it.moveCamera(cameraUpdate)
                }
                AlertDialog.Builder(this)
                    .setView(mapView)
                    .show()
            }
        }


    }
    // 뒤로가기 눌렀을 때 동작
    override fun onBackPressed() {
        super.onBackPressed()
        // 메모 DB 갱신
        viewModel?.addOrUpdateMemo(this)
    }

    //private var viewModel: DetailVIewModel? = null
    //private val dialogCalendar = Calendar.getInstance()
    // 날짜 다이얼로그를 여는 함
    private fun openDateDialog() {
        // android.app 의 클래스를 임포트 하면 안되고 build.gradle 에서 추가했던 com.takisoft.datetimepicker 의 DatePickerDialog 로 생성해야 됨
        val datePickerDialog = DatePickerDialog(this)
        // 사용자에 의해 날짜가 입력되면 실행되는 리스너
        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            dialogCalendar.set(year, month, dayOfMonth) // 임시 캘린더 변수에 값을 저장하고
            openTimeDialog() // 시간을 설정하는 다이얼로그를 열도록 함
        }
        datePickerDialog.show() // 만들어진 다이얼로그 연다.
    }

    // 시간 다이얼로그를 여는 함수
    private fun openTimeDialog() {
        // 생성자 안에서 리스너를 구현(등록)하여 시간이 입력 됐을 때 앞에서 날짜가 입력됐었던 임시 캘린더 변수에 사용자가 입력한 시간을 설정하고
        // 캘린더 변수의 time 값(Date 객체) 를 viewModel 에 새 알람값으로 설정해 준다.
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                dialogCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dialogCalendar.set(Calendar.MINUTE, minute)

                viewModel?.setAlarm(dialogCalendar.time)
            }, 0, 0, false
        ) // 다이얼로그의 초기시간은 0시 0분으로 설정함, 24시간제는 사용하지 않
        timePickerDialog.show() // 만들어진 다이얼로그를 화면에 띄움
    }

    // activity 에서 사용할 메뉴를 설정하는 함수를 override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true // 메뉴를 사용하겠다는 의미로 true 반환
    }

    // 메뉴 아이템을 선택했을 때 실행되는 함수
    @SuppressLint("MissingPermission") // IntroActivity 에서 이미 체크한 위치 권한 허용 여부를 다시 체크하지 않기 위해서 함수에 노테이션 추가
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) // 선택된 메뉴의 id 에 따라 분기
        {
            R.id.menu_share -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, supportActionBar?.title)
                intent.putExtra(Intent.EXTRA_TEXT, contentEdit.text.toString())

                startActivity(intent)
            }
            R.id.menu_alarm -> {
                if (viewModel?.memoData?.alarmTime!!.after(Date())) { // 기존의 알람값이 현재 시간 기준으로 유효한지 체크
                    AlertDialog.Builder(this) // 알람을 재설정할 것인지 삭제할 것인지 묻는 다이얼로그 띄
                        .setTitle("안내")
                        .setMessage("기존에 알람이 설정되어 있습니다. 삭제 또는 재설정할 수 있습니다.")
                        .setPositiveButton("재설정", DialogInterface.OnClickListener { dialog, which ->
                            openDateDialog()
                        })
                        .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                            viewModel?.deleteAlarm() // alarmTime 초기화
                        })
                        .show()
                } else {
                    openDateDialog() // 유효하지 않다면 날짜 다이얼로그를 띄워 알람값을 바로 설정하도록 함
                }
            }
            R.id.menu_location -> {
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 위치를 메모에 저장하거나 삭제할 수 있습니다.")
                    .setPositiveButton("위치지정", DialogInterface.OnClickListener { dialog, which ->
                        // LocationManager 를 가져와서 위치기능이 켜져있는지 확인 (GPS 및 네트워크 위치 기능을 모두 확인해야함)
                        val locationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnabled =
                            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnabled =
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        // 위치기능이 둘 다 꺼진 경우, 스낵바를 띄워 시스템의 위치옵션화면을 안내해줌
                        if (!isGPSEnabled && !isNetworkEnabled) {
                            Snackbar.make(
                                toolbar_layout,
                                "폰의 위치기능을 켜야 기능을 사용할 수 있습니다.",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("설정", View.OnClickListener {
                                    val goToSettings =
                                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(goToSettings)
                                }).show()
                        } else { // 하나라도 켜져있으면 Criteria 객체에 위치 정확도와 배터리 소모량을 설정.
                            val criteria = Criteria()
                            criteria.accuracy = Criteria.ACCURACY_MEDIUM
                            criteria.powerRequirement = Criteria.POWER_MEDIUM
                            // locationManager.requestSingleUpdate : 위치정보를 1회 받아온다.
                            // 위치값을 받을 수 있는 LocationListener 타입의 object 를 구현하여 넘겨줘야한다.(override 할 함수 4개)
                            locationManager.requestSingleUpdate(
                                criteria,
                                object : LocationListener {
                                    // 위치정보가 갱신될때 실행, 갱신된 위치값을 ViewModel 에 넘겨준다.
                                    override fun onLocationChanged(location: Location?) {
                                        location?.run {
                                            viewModel!!.setLocation(latitude, longitude)
                                        }
                                    }

                                    override fun onProviderEnabled(provider: String?) {
                                    }

                                    override fun onProviderDisabled(provider: String?) {
                                    }

                                    override fun onStatusChanged(
                                        provider: String?,
                                        status: Int,
                                        extras: Bundle?
                                    ) {
                                    }
                                },
                                null
                            )

                        }
                    })
                    .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.setLocation(0.0, 0.0)
                    }).show()
            }
            R.id.menu_weather -> {
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("현재 날씨를 메모에 저장하거나 삭제할 수 있습니다.")
                    .setPositiveButton("날씨 가져오기", DialogInterface.OnClickListener { dialog, which ->
                        val locationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGPSEnabled =
                            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        val isNetworkEnabled =
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        // 위치기능이 둘 다 꺼진 경우, 스낵바를 띄워 시스템의 위치옵션화면을 안내해줌
                        if (!isGPSEnabled && !isNetworkEnabled) {
                            Snackbar.make(
                                toolbar_layout,
                                "폰의 위치기능을 켜야 기능을 사용할 수 있습니다.",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("설정", View.OnClickListener {
                                    val goToSettings =
                                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(goToSettings)
                                }).show()
                        } else { // 하나라도 켜져있으면 Criteria 객체에 위치 정확도와 배터리 소모량을 설정.
                            val criteria = Criteria()
                            criteria.accuracy = Criteria.ACCURACY_MEDIUM
                            criteria.powerRequirement = Criteria.POWER_MEDIUM
                            // locationManager.requestSingleUpdate : 위치정보를 1회 받아온다.
                            // 위치값을 받을 수 있는 LocationListener 타입의 object 를 구현하여 넘겨줘야한다.(override 할 함수 4개)
                            locationManager.requestSingleUpdate(
                                criteria,
                                object : LocationListener {
                                    // 위치정보가 갱신될때 실행, 갱신된 위치값을 ViewModel 에 넘겨준다.
                                    override fun onLocationChanged(location: Location?) {
                                        location?.run {
                                            viewModel!!.setWeather(latitude, longitude)
                                        }
                                    }

                                    override fun onProviderEnabled(provider: String?) {}
                                    override fun onProviderDisabled(provider: String?) {}
                                    override fun onStatusChanged(
                                        provider: String?,
                                        status: Int,
                                        extras: Bundle?
                                    ) {
                                    }
                                },
                                null
                            )
                        }
                    }).setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        viewModel!!.deleteWeather()
                    }).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                // 결과값으로 들어온 데이터를 비트맵으로 변환함
                val inputStream = data?.data?.let { contentResolver.openInputStream(it) }
                inputStream?.let {
                    val image = BitmapFactory.decodeStream(it)
                    //bgImage 에 표시되는 이미지를 null 로 초기화하고 새 이미지를 viewModel 에 설정함.
                    bgImage.setImageURI(null)
                    image?.let { viewModel?.setImageFile(this, it) }
                    // 작업이 끝나면 input 스트림 닫아줌
                    it.close()
                }

            } catch (e: Exception) {
                println(e)
            }
        }
    }

}
