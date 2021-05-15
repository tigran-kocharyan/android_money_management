package ru.totowka.accountant.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import ru.totowka.accountant.data.type.ChartTransactionState
import java.time.LocalDate
import java.time.YearMonth

class AnalysisFragment : Fragment() {
    private val controller = Controller()
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var barChart: BarChart
    private val year = LocalDate.now().year
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.top_bar_analysis)
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.refresh -> {
                        drawData()
                        true
                    }
                    else -> false
                }
            }
        barChart = view.findViewById(R.id.bar_chart)
        view.findViewById<Spinner>(R.id.start_date).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    startDate = LocalDate.of(LocalDate.now().year, position + 1, 1)
                }
            }
        view.findViewById<Spinner>(R.id.end_date).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    endDate = LocalDate.of(
                        year,
                        position + 1,
                        YearMonth.of(year, position + 1).lengthOfMonth()
                    )
                }
            }
        return view
    }

    private fun drawData() {
        if (startDate.month < endDate.month ) {
            lifecycleScope.launch {
                val chartStates = controller.getChartTransactionStatesByDateRange(
                    startDate.atStartOfDay(),
                    endDate.atTime(23,59)
                )
                val resultedStates = createTotalState(chartStates)
                val entries = ArrayList<BarEntry>()

                for (i in 0..30) {
                    entries.add(BarEntry(i.toFloat(), resultedStates[i].toFloat()))
                }

                val set = BarDataSet(entries, "Difference between expenses")
                val data = BarData(set)
                data.barWidth = 0.5f
                barChart.data = data;
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate(); // refresh
                barChart.setNoDataText("Refresh chart")
                barChart.setDrawBorders(true)
            }
        } else {
            Toast.makeText(
                requireContext(),
                "End Date should be bigger than Start Date0",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createTotalState(values: List<ChartTransactionState>) : ArrayList<Double> {
        val startMonth = ArrayList<ChartTransactionState>().apply {
            for (i in 1..YearMonth.of(year, startDate.month).lengthOfMonth()) {
                add(ChartTransactionState(0.0, LocalDate.of(year, startDate.month, i)))
            }
            while(size < 31) {
                add(ChartTransactionState(0.0, LocalDate.of(year, 8, size)))
            }
        }
        val endMonth = ArrayList<ChartTransactionState>().apply {
            for (i in 1..YearMonth.of(year, endDate.month).lengthOfMonth()) {
                add(ChartTransactionState(0.0, LocalDate.of(year, endDate.month, i)))
            }
            while(size < 31) {
                add(ChartTransactionState(0.0, LocalDate.of(year, 8, size)))
            }
        }
        for (value in values) {
            if (value.date.month == startDate.month) {
                startMonth[value.date.dayOfMonth-1] = value
            } else {
                endMonth[value.date.dayOfMonth-1] = value
            }
        }
        return ArrayList<Double>().apply {
            for (i in 0..30) {
                add(endMonth[i].total - startMonth[i].total)
            }
        }
    }
}
//    private fun drawData() {
//        lifecycleScope.launch {
//            val chartStates = controller.getChartTransactionStates(TimeFilter.ALL_TIME)
//            if (chartStates.size >= 5) {
//                val keys = ArrayList<String>()
//                val names = ArrayList<String>()
//                val colors = ArrayList<Int>()
//                val items = ArrayList<ChartItem>()
//
//                keys.add("Dates");
//                names.add("Transactions Total Sum");
//                colors.add(Color.RED);
//                for (state in chartStates) {
//                    items.add(ChartItem(state.date, listOf(state.total.toInt())));
//                }
//                val chartData = ChartData(keys, names, colors, items)
//                view?.findViewById<TChart>(R.id.tchart)?.setData(chartData)
//                view?.findViewById<TChart>(R.id.tchart)?.visibility = View.VISIBLE
//            } else {
//                view?.findViewById<TChart>(R.id.tchart)?.visibility  = View.INVISIBLE
//                Toast.makeText(
//                    requireContext(),
//                    "Transactions size: ${chartStates.size}\nMake it at least 5.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

//    private fun drawMPAndroidChart() {
//        val chartStates = controller.getChartTransactionStates(TimeFilter.ALL_TIME)
//       val chart = view.findViewById<LineChart>(R.id.linechart)
//        chart.setOnChartValueSelectedListener {}
//        chart.onChartGestureListener

//        chart.isDragEnabled = false
//        chart.setScaleEnabled(false)
//
//        val values = ArrayList<Entry>()
//        values.add(Entry(0f, 60f))
//        values.add(Entry(1f, 30f))
//        values.add(Entry(2f, 40f))
//        values.add(Entry(3f, 90f))
//        values.add(Entry(4f, 10f))
//
//        val dataSet = LineDataSet(values, "Data Set #1")
//        dataSet.fillAlpha = 255
//        dataSet.color = Color.RED
//        dataSet.lineWidth = 3f
//        dataSet.valueTextSize = 10f
//        dataSet.fillColor = Color.BLACK
//
//        val dataSets = ArrayList<ILineDataSet>()
//        dataSets.add(dataSet)
//
//        val lineData = LineData(dataSets)
//        chart.data = lineData
//    }