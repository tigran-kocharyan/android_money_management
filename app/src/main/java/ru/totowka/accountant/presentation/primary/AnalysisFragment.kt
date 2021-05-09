package ru.totowka.accountant.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.klim.tcharts.TChart
import com.klim.tcharts.entities.ChartData
import com.klim.tcharts.entities.ChartItem
import ru.totowka.accountant.Controller
import ru.totowka.accountant.R
import java.util.*
import kotlin.collections.ArrayList

class AnalysisFragment : Fragment() {
    val controller = Controller()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        val keys = ArrayList<String>(); //keys for each chart
        val names = ArrayList<String>(); //names for chart
        val colors = ArrayList<Int>(); //colors for lines
        val items = ArrayList<ChartItem>(); //charts value for some time

        keys.add("y0");
        keys.add("y1");
        names.add("Red Line");
        names.add("Green Line");
        colors.add(Color.RED);
        colors.add(Color.GREEN);

        var startTime = 1614542230000L;
        val random = Random();
        for (i in 0..99) {
            startTime += 86_400_000;

            val values = ArrayList<Int>();
            for (j in 0 until keys.size) {
                values.add(random.nextInt(1000));
            }

            val chartItem = ChartItem(startTime, values);
            items.add(chartItem);
        }
        val chartData = ChartData(keys, names, colors, items)

        view.findViewById<TChart>(R.id.tchart).id = View.generateViewId()
        view.findViewById<TChart>(R.id.tchart).setData(chartData)
        view.findViewById<TChart>(R.id.tchart).setTitle("Chart")
        return view
    }


//    private fun drawData() {
//        lifecycleScope.launch {
//
//
//        }
//    }

    private fun drawMPAndroidChart() {
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
    }
}