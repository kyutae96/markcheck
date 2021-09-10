package com.example.markcheck

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF.length
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_google_map.*
import kotlinx.android.synthetic.main.activity_map_main.*
import kotlinx.android.synthetic.main.activity_xml.*
import kotlinx.android.synthetic.main.main_toolbar.*
import java.nio.file.Files.size
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class GoogleMapActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var mBackWait: Long = 0
    val handler = Handler(Looper.getMainLooper()) {
        true
    }
    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val REQUEST_PERMISSION_CODE = 1
    val DEFAULT_ZOOM_LEVEL = 17f
    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_main)

        myLocationButton.setOnClickListener { onMyLocationButtonClick() }

        mapView.onCreate(savedInstanceState)

        if (checkPermissions()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

        main_navigationView.setNavigationItemSelectedListener(this)
        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_account) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        val thread = Thread(PagerRunnable())
        thread.start()
        card_view.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        initMap()
    }

    private fun checkPermissions(): Boolean {

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun initMap() {
        mapView.getMapAsync {
            intent.hasExtra("latitude")
            intent.hasExtra("longitude")
            intent.hasExtra("name")
            intent.hasExtra("address")
            intent.hasExtra("personnumber")
            val strlatitude = intent.getSerializableExtra("latitude") as ArrayList<*>
            val strlongitude = intent.getSerializableExtra("longitude") as ArrayList<*>
            val name = intent.getSerializableExtra("name") as ArrayList<*>
            val address = intent.getSerializableExtra("address") as ArrayList<*>
            val personnumber = intent.getSerializableExtra("personnumber") as ArrayList<*>

            val lastnum = strlatitude.size - 1
            val CITY_HALL = LatLng(37.52487, 126.92723)

            for (i in 0..lastnum) {
                val makerOptions = MarkerOptions()


                makerOptions.title(name[i].toString())
                makerOptions.position(LatLng(strlatitude[i].toString().toDouble(),strlongitude[i].toString().toDouble()))
                makerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))

                googleMap = it


                val marker: Marker = it.addMarker(makerOptions)
                //name, address, personnumber String 값, 배열 marker.tag으로 묶음
                marker.tag = name[i].toString() + "/" + address[i].toString() + "/" + personnumber[i].toString()

                it.uiSettings.isMyLocationButtonEnabled = false
                when {
                    checkPermissions() -> {
                        it.isMyLocationEnabled = true
                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                getMyLocation(),
                                DEFAULT_ZOOM_LEVEL
                            )
                        )
                    }
                    else -> {
                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                CITY_HALL,
                                DEFAULT_ZOOM_LEVEL
                            )
                        )
                    }
                }
            }

            it.setOnMarkerClickListener { marker ->
                card_view.visibility = View.VISIBLE
                //name, address, personnumber로 구성된 marker.tag 배열 split
                val arr = marker.tag.toString().split("/")

                mark_name.text = arr[0]
                mark_what.text = arr[1]
                mark_add_lot.text = arr[1]
                phone_num.text = arr[2]

                false
            }
            //맵 클릭 리스너-맵 클릭하면 카드뷰 없어짐
            it.setOnMapClickListener {
                card_view.visibility = View.GONE
            }
        }

    }


    fun more_btn(view: View) {
        val intent = Intent(this, ChartActivity::class.java)
        startActivity(intent)
    }


    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {
        val locationProvider: String = LocationManager.GPS_PROVIDER
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocation: Location = locationManager.getLastKnownLocation(locationProvider)!!
        return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    }

    private fun onMyLocationButtonClick() {
        when {
            checkPermissions() ->
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL)
                )
            else ->
                Toast.makeText(applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    //2초 마다 페이지 넘기기
    inner class PagerRunnable : Runnable {
        override fun run() {
            while (true) {
                Thread.sleep(2000)
                handler.sendEmptyMessage(0)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // 메뉴 버튼
                main_drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                Toast.makeText(this, "마이페이지", Toast.LENGTH_SHORT).show()
            }
            R.id.item2 -> {
                Toast.makeText(this, "분야로 찾기", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfessionalActivity::class.java)
                startActivity(intent)
            }
            R.id.item3 -> {
                Toast.makeText(this, "이용 안내", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HowActivity::class.java)
                startActivity(intent)
            }
            R.id.item4 -> {
                Toast.makeText(this, "공지사항", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }
            R.id.item5 -> {
                Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, XmlActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return false
    }

    override fun onBackPressed() { //뒤로가기 처리
        when {
            main_drawer_layout.isDrawerOpen(GravityCompat.START) -> {
                main_drawer_layout.closeDrawers()
            }
            card_view.isVisible -> {
                card_view.visibility = View.INVISIBLE
            }
            System.currentTimeMillis() - mBackWait >= 2000 -> {
                mBackWait = System.currentTimeMillis()
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //앱 강제종료 -> stack 까지 사라지는건지? finish() 상위호환인지?
                finishAffinity()
                System.runFinalization()
                exitProcess(0)

            }
        }

    }
}