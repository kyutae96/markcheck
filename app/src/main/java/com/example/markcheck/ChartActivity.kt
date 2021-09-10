package com.example.markcheck

import android.graphics.Color
import android.hardware.lights.Light
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.android.synthetic.main.activity_login.*


class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        var radarChart: RadarChart = findViewById(R.id.radar_chart)
        radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        val entries: ArrayList<RadarEntry> = ArrayList()
        entries.add(RadarEntry(20F))
        entries.add(RadarEntry(15F))
        entries.add(RadarEntry(18F))
        entries.add(RadarEntry(16F))
        entries.add(RadarEntry(20F))
        entries.add(RadarEntry(17F))


        val colorsItems = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for (c in COLORFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        val dataSet = RadarDataSet(entries, "")
        dataSet.color = Color.rgb(121, 162, 175)
        dataSet.fillColor = Color.rgb(121, 162, 175)
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 180
        dataSet.lineWidth = 2f
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE
        dataSet.isDrawHighlightCircleEnabled
        dataSet.setDrawHighlightIndicators(false)

        val data = RadarData()
        data.setValueTextColor(Color.WHITE)
        data.setDrawValues(false)
        data.addDataSet(dataSet)

        val labels = arrayOf("지역", "경력", "전문분야", "수임건수", "승소율", "평점")

        val xAxis: XAxis = radarChart.xAxis
        xAxis.textSize = 14f
        xAxis.textColor = Color.WHITE
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val yAxis: YAxis = radarChart.yAxis
        yAxis.setLabelCount(6, false)
        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)

        val l: Legend = radarChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 5f
        l.textColor = Color.WHITE


        radarChart.data = data

    }


}