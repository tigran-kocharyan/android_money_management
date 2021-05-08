package ru.totowka.accountant.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.activity_add_transaction.*
import ru.totowka.accountant.DateTimeUtils.Companion.toDate
import ru.totowka.accountant.R
import ru.totowka.accountant.data.type.Product
import ru.totowka.accountant.data.type.Transaction
import ru.totowka.accountant.presentation.adapter.ProductAdapter
import ru.totowka.accountant.presentation.secondary.insert.ProductDialogFragment
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class AddTransactionActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener, ProductDialogFragment.OnCompleteListener,
    TimePickerDialog.OnTimeSetListener {

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    var dayPick = calendar.get(Calendar.DAY_OF_MONTH)
    var monthPick = calendar.get(Calendar.MONTH)
    var yearPick = calendar.get(Calendar.YEAR)
    var hourPick = calendar.get(Calendar.HOUR)
    var minutePick = calendar.get(Calendar.MINUTE)
    lateinit var time: Date
    var isPicked = false

    lateinit var products: RecyclerView
    var product_items = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        products = findViewById(R.id.products)
        products.layoutManager = LinearLayoutManager(this);
        products.adapter = ProductAdapter(product_items)

        add_product.setOnClickListener(this)
        picker.setOnClickListener(this)
        button_save.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_product -> ProductDialogFragment()
                .show(supportFragmentManager, "ProductInputDialogFragment")
            R.id.picker -> DatePickerDialog(this, this, yearPick, monthPick, dayPick).show()
            R.id.button_save -> {
                if (isPicked) {
                    val intent = Intent()
                    val transaction = Transaction(
                        transaction_title.text.toString(),
                        Timestamp(time),
                        product_items
                    ).apply { total = items.map { it.total }.sum() }
                    intent.putExtra("transaction", transaction)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    DatePickerDialog(this, this, yearPick, monthPick, dayPick).show()
                }
            }
        }
    }

    override fun onComplete(product: Product) {
        product_items.add(product)
        products.adapter?.notifyDataSetChanged()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dayPick = dayOfMonth
        monthPick = month
        yearPick = year

        TimePickerDialog(this, this, hourPick, minutePick, true).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        hourPick = hourOfDay
        minutePick = minute
        isPicked = true
        time = getDate()
        date.text = "Time: ${dayPick}-${monthPick + 1}-${yearPick} ${hourPick}:${minutePick}"
    }

    private fun getDate(): Date = toDate(
        LocalDateTime.of(yearPick, monthPick+1, dayPick, hourPick, minutePick)
    )
}