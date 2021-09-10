package com.example.markcheck

import android.R.attr
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.nfc.Tag
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_xml.*
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.util.*
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.LatLng

import android.R.attr.button
import android.app.Person
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_google_map.*
import kotlinx.android.synthetic.main.activity_professional.*


class ProfessionalActivity : AppCompatActivity() {

    private var vpAdapter: FragmentStatePagerAdapter? = null
    var currentPosition = 0
    val handler = Handler(Looper.getMainLooper()) {
        setPage()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional)

        vpAdapter = CustomPagerAdapter(supportFragmentManager)
        viewpager.adapter = vpAdapter

        indicator.setViewPager(viewpager)


        val thread = Thread(PagerRunnable())
        thread.start()

    }

    fun patent_btn(view: View){
        Toast.makeText(this, "특허/실용 버튼", Toast.LENGTH_SHORT).show()
    }

    fun marker_btn(view: View){
        Toast.makeText(this, "상표 버튼", Toast.LENGTH_SHORT).show()
    }

    fun design_btn(view: View){
        Toast.makeText(this, "디자인 버튼", Toast.LENGTH_SHORT).show()
    }


    class CustomPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val PAGENUMBER = 4

        override fun getCount(): Int {
            return PAGENUMBER
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TestFragment.newInstance(R.drawable.mark_logo, "")
                1 -> TestFragment.newInstance(R.drawable.marktong, "")
                2 -> TestFragment.newInstance(R.drawable.mark_logo, "")
                else -> TestFragment.newInstance(R.drawable.marktong, "")
            }
        }
    }

    fun setPage() {
        if (currentPosition == 5) currentPosition = 0
        viewpager.setCurrentItem(currentPosition, true)
        currentPosition += 1
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


}