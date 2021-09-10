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
import com.google.android.gms.location.LocationServices
import kotlin.collections.ArrayList


class XmlActivity : AppCompatActivity() {

    val geocoder = Geocoder(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml)

        var textView = findViewById<TextView>(R.id.textView)
        textView.text = ""
        init()
    }

    private fun init() {
        showProgress(false)
    }


    fun showProgress(isShow: Boolean) {
        if (isShow) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE
    }


    inner class NetworkThread : Thread() {

        override fun run() {

            try {
                var edit_text = edit_text.text.toString()

                var site =
                    "http://plus.kipris.or.kr/openapi/rest/CommonSearchService/CommonSearchAgentInfo?searchAddress=" +
                            edit_text +
                            "&docsStart=1&accessKey=Eir=7ZOQhPOeHEvArbBv79keP9LK8wY9=iZxbjbb1Ak="
                var url = URL(site)
                var conn = url.openConnection()
                var input = conn.getInputStream()

                var factory = DocumentBuilderFactory.newInstance()
                var builder = factory.newDocumentBuilder()
                var doc = builder.parse(input)

                var root = doc.documentElement

                var item_node_list = root.getElementsByTagName("commonSearchPersonInfo")

                var lati = ArrayList<String>()
                var loti = ArrayList<String>()
                var name = ArrayList<String>()
                var address = ArrayList<String>()
                var personnumber = ArrayList<String>()

                for (i in 0 until item_node_list.length) {
                    var item_element = item_node_list.item(i) as Element

                    var IndexNumber_list = item_element.getElementsByTagName("IndexNumber")
                    var PersonNumber_list = item_element.getElementsByTagName("PersonNumber")
                    var Address_list = item_element.getElementsByTagName("Address")
                    var Name_list = item_element.getElementsByTagName("Name")

                    var IndexNumber_node = IndexNumber_list.item(0) as Element
                    var PersonNumber_node = PersonNumber_list.item(0) as Element
                    var Address_node = Address_list.item(0) as Element
                    var Name_node = Name_list.item(0) as Element

                    var IndexNumber = IndexNumber_node.textContent
                    var PersonNumber = PersonNumber_node.textContent
                    var Address = Address_node.textContent
                    var city_Address_node = Address.split(' ')
                    var Name = Name_node.textContent

                    val strAddress = geocoder.getFromLocationName(Address.toString(), 1)
                    val stilaAddress = strAddress[0].latitude.toString()
                    val stiloAddress = strAddress[0].longitude.toString()
                    Log.d("위도", stilaAddress)
                    Log.d("경도", stiloAddress)


                    runOnUiThread {
                        textView.append("IndexNumber : ${IndexNumber}\n")
                        textView.append("PersonNumber: ${PersonNumber}\n")
                        textView.append("시 : ${city_Address_node[0]}\n")
                        textView.append("구 : ${city_Address_node[1]}\n")
                        textView.append("위도 : ${stilaAddress}\n")
                        textView.append("경도 : ${stiloAddress}\n")
                        textView.append("Name: ${Name}\n\n")
                    }

                    lati.add(stilaAddress)
                    loti.add(stiloAddress)
                    name.add(Name)
                    address.add(Address)
                    personnumber.add(PersonNumber)

                }

                val intent = Intent(applicationContext, GoogleMapActivity::class.java)
                intent.putExtra("latitude", lati)
                intent.putExtra("longitude", loti)
                intent.putExtra("name", name)
                intent.putExtra("address", address)
                intent.putExtra("personnumber", personnumber)

                startActivity(intent)
                showProgress(false)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun button(view: View) {
        showProgress(true)
//        var thread = NetworkThread()
        NetworkThread().start()

    }
}