package com.sherepenko.android.measureit.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sherepenko.android.measureit.R
import com.sherepenko.android.measureit.data.HumidityItem
import com.sherepenko.android.measureit.data.PressureItem
import com.sherepenko.android.measureit.data.Status
import com.sherepenko.android.measureit.data.TemperatureItem
import com.sherepenko.android.measureit.data.isNullOrEmpty
import com.sherepenko.android.measureit.viewmodels.DashboardViewModel
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.fragment_dashboard.humidityChartView
import kotlinx.android.synthetic.main.fragment_dashboard.humidityLoadingView
import kotlinx.android.synthetic.main.fragment_dashboard.humidityMaxValueView
import kotlinx.android.synthetic.main.fragment_dashboard.humidityMinValueView
import kotlinx.android.synthetic.main.fragment_dashboard.humidityValueView
import kotlinx.android.synthetic.main.fragment_dashboard.pressureChartView
import kotlinx.android.synthetic.main.fragment_dashboard.pressureLoadingView
import kotlinx.android.synthetic.main.fragment_dashboard.pressureMaxValueView
import kotlinx.android.synthetic.main.fragment_dashboard.pressureMinValueView
import kotlinx.android.synthetic.main.fragment_dashboard.pressureValueView
import kotlinx.android.synthetic.main.fragment_dashboard.temperatureChartView
import kotlinx.android.synthetic.main.fragment_dashboard.temperatureLoadingView
import kotlinx.android.synthetic.main.fragment_dashboard.temperatureMaxValueView
import kotlinx.android.synthetic.main.fragment_dashboard.temperatureMinValueView
import kotlinx.android.synthetic.main.fragment_dashboard.temperatureValueView
import kotlinx.android.synthetic.main.fragment_dashboard.toolbarView
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    companion object {
        private const val CHART_ANIMATION_DURATION_MS = 350
    }

    private val dashboardViewModel: DashboardViewModel by viewModel()

    private val startTime: Long = Instant.now().toEpochMilli()

    private var minHumidityValue = Double.NaN
    private var maxHumidityValue = Double.NaN

    private var minPressureValue = Double.NaN
    private var maxPressureValue = Double.NaN

    private var minTemperatureValue = Double.NaN
    private var maxTemperatureValue = Double.NaN

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setupHumidity()
        setupPressure()
        setupTemperature()
    }

    private fun setupToolbar() {
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarView)
        }
    }

    private fun setupHumidity() {
        humidityChartView.setupChart(
            createDataSet(
                "Humidity",
                ResourcesCompat.getColor(resources, R.color.colorHumidity, null)
            )
        )

        dashboardViewModel.getHumidity().observe(
            viewLifecycleOwner,
            {
                when (it.status) {
                    Status.LOADING -> {
                        // ignore
                    }
                    Status.SUCCESS -> {
                        checkNotNull(it.data)
                        updateHumidity(it.data)
                    }
                    Status.ERROR -> {
                        updateHumidity(it.data)
                    }
                }
            }
        )
    }

    private fun setupPressure() {
        pressureChartView.setupChart(
            createDataSet(
                "Pressure",
                ResourcesCompat.getColor(resources, R.color.colorPressure, null)
            )
        )

        dashboardViewModel.getPressure().observe(
            viewLifecycleOwner,
            {
                when (it.status) {
                    Status.LOADING -> {
                        // ignore
                    }
                    Status.SUCCESS -> {
                        checkNotNull(it.data)
                        updatePressure(it.data)
                    }
                    Status.ERROR -> {
                        updatePressure(it.data)
                    }
                }
            }
        )
    }

    private fun setupTemperature() {
        temperatureChartView.setupChart(
            createDataSet(
                "Temperature",
                ResourcesCompat.getColor(resources, R.color.colorTemperature, null)
            )
        )

        dashboardViewModel.getTemperature().observe(
            viewLifecycleOwner,
            {
                when (it.status) {
                    Status.LOADING -> {
                        // ignore
                    }
                    Status.SUCCESS -> {
                        checkNotNull(it.data)
                        updateTemperature(it.data)
                    }
                    Status.ERROR -> {
                        updateTemperature(it.data)
                    }
                }
            }
        )
    }

    private fun updateHumidity(item: HumidityItem?) {
        if (!item.isNullOrEmpty()) {
            checkNotNull(item)

            humidityValueView.apply {
                text = getString(R.string.formatted_value, item.value)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorSuccess, null))
            }

            updateMinHumidityValue(item)
            updateMaxHumidityValue(item)
            updateHumidityChart(item)

            humidityLoadingView.visibility = View.GONE
        } else {
            humidityValueView.apply {
                text = getString(R.string.unknown_value)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorError, null))
            }
        }
    }

    private fun updateMinHumidityValue(item: HumidityItem) {
        if (minHumidityValue.isNaN() || minHumidityValue > item.value) {
            minHumidityValue = item.value
            humidityMinValueView.apply {
                text = getString(R.string.formatted_value, minHumidityValue)
            }
        }
    }

    private fun updateMaxHumidityValue(item: HumidityItem) {
        if (maxHumidityValue.isNaN() || maxHumidityValue < item.value) {
            maxHumidityValue = item.value
            humidityMaxValueView.apply {
                text = getString(R.string.formatted_value, maxHumidityValue)
            }
        }
    }

    private fun updateHumidityChart(item: HumidityItem) {
        humidityChartView.addEntry(
            Entry(
                Instant.now().minusMillis(startTime).toEpochMilli().toFloat(),
                item.value.toFloat()
            )
        )
    }

    private fun updatePressure(item: PressureItem?) {
        if (!item.isNullOrEmpty()) {
            checkNotNull(item)

            pressureValueView.apply {
                text = getString(R.string.formatted_value, item.value)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorSuccess, null))
            }

            updateMinPressureValue(item)
            updateMaxPressureValue(item)
            updatePressureChart(item)

            pressureLoadingView.visibility = View.GONE
        } else {
            pressureValueView.apply {
                text = getString(R.string.unknown_value)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorError, null))
            }
        }
    }

    private fun updateMinPressureValue(item: PressureItem) {
        if (minPressureValue.isNaN() || minPressureValue > item.value) {
            minPressureValue = item.value
            pressureMinValueView.apply {
                text = getString(R.string.formatted_value, minPressureValue)
            }
        }
    }

    private fun updateMaxPressureValue(item: PressureItem) {
        if (maxPressureValue.isNaN() || maxPressureValue < item.value) {
            maxPressureValue = item.value
            pressureMaxValueView.apply {
                text = getString(R.string.formatted_value, maxPressureValue)
            }
        }
    }

    private fun updatePressureChart(item: PressureItem) {
        pressureChartView.addEntry(
            Entry(
                Instant.now().minusMillis(startTime).toEpochMilli().toFloat(),
                item.value.toFloat()
            )
        )
    }

    private fun updateTemperature(item: TemperatureItem?) {
        if (!item.isNullOrEmpty()) {
            checkNotNull(item)

            temperatureValueView.apply {
                text = getString(R.string.formatted_value, item.value)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorSuccess, null))
            }

            updateMinTemperatureValue(item)
            updateMaxTemperatureValue(item)
            updateTemperatureChart(item)

            temperatureLoadingView.visibility = View.GONE
        } else {
            temperatureValueView.apply {
                text = getString(R.string.unknown_value)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorError, null))
            }
        }
    }

    private fun updateMinTemperatureValue(item: TemperatureItem) {
        if (minTemperatureValue.isNaN() || minTemperatureValue > item.value) {
            minTemperatureValue = item.value
            temperatureMinValueView.apply {
                text = getString(R.string.formatted_value, minTemperatureValue)
            }
        }
    }

    private fun updateMaxTemperatureValue(item: TemperatureItem) {
        if (maxTemperatureValue.isNaN() || maxTemperatureValue < item.value) {
            maxTemperatureValue = item.value
            temperatureMaxValueView.apply {
                text = getString(R.string.formatted_value, maxTemperatureValue)
            }
        }
    }

    private fun updateTemperatureChart(item: TemperatureItem) {
        temperatureChartView.addEntry(
            Entry(
                Instant.now().minusMillis(startTime).toEpochMilli().toFloat(),
                item.value.toFloat()
            )
        )
    }

    private fun createDataSet(
        label: String,
        chartColor: Int,
        values: List<Entry> = mutableListOf()
    ): LineDataSet =
        LineDataSet(values, label).apply {
            setDrawCircles(false)
            setDrawValues(false)
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            color = chartColor
            fillColor = chartColor
            valueTextColor = chartColor
            lineWidth = 1.5f
        }

    private fun LineChart.setupChart(dataSet: LineDataSet) {
        setNoDataText(null)
        setTouchEnabled(false)
        setScaleEnabled(false)

        isDragEnabled = false

        axisLeft.apply {
            setDrawAxisLine(false)
            setDrawGridLines(true)
            gridColor = ResourcesCompat.getColor(resources, R.color.colorGrid, null)
            textColor = ResourcesCompat.getColor(resources, R.color.textColorSecondary, null)
            textSize = ResourcesCompat.getFloat(resources, R.dimen.dashboard_chart_font_size)
        }

        axisRight.isEnabled = false
        xAxis.isEnabled = false

        description.isEnabled = false
        legend.isEnabled = false

        data = LineData(dataSet).apply {
            setDrawValues(false)
        }

        animateXY(CHART_ANIMATION_DURATION_MS, CHART_ANIMATION_DURATION_MS)
        invalidate()
    }

    private fun LineChart.addEntry(entry: Entry) {
        data.dataSets[0].addEntry(entry)
        data.notifyDataChanged()
        notifyDataSetChanged()
        setVisibleXRangeMaximum(TimeUnit.SECONDS.toMillis(30).toFloat())
        moveViewToX(entry.x)
    }
}
