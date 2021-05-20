package ru.totowka.accountant.presentation.primary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
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
        if (startDate.month < endDate.month) {
            lifecycleScope.launch {
                val chartStates = controller.getChartTransactionStatesByDateRange(
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59)
                )
                val resultedStates = controller.createTotalState(chartStates, startDate, endDate)
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
                "End Date should be bigger than Start Date",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}