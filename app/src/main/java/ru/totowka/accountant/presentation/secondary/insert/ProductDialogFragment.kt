package ru.totowka.accountant.presentation.secondary.insert

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_product.view.*
import ru.totowka.accountant.R
import ru.totowka.accountant.data.type.Product
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class ProductDialogFragment : DialogFragment(), View.OnClickListener {
    lateinit var mListener: OnCompleteListener
    lateinit var total: TextView
    lateinit var title: EditText
    lateinit var amount: EditText
    lateinit var price: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            mListener = (activity as OnCompleteListener?)!!
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + " must implement OnCompleteListener"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dialog_product, container, false)
        title = view.findViewById(R.id.product_title)
        total = view.findViewById(R.id.product_total)
        amount = view.findViewById(R.id.product_amount)
        price = view.findViewById(R.id.product_price)

//        amount.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (amount.text.toString().isBlank() || price.text.toString().isBlank()) {
//                    try {
//                        val price = parseDouble(price.text.toString())
//                        val amount = parseInt(amount.text.toString())
//                        total.text = "${price * amount} ₽"
//                    } catch (ignored: NumberFormatException) { }
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//        })
//
//        price.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (amount.text.toString().isBlank() || s.) {
//                    try {
//                        total.text = "${parseDouble(price.text.toString()) * parseInt(amount.text.toString())} ₽"
//                    } catch (ignored: NumberFormatException) { }
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//        })

        view.cancelButton.setOnClickListener(this)
        view.submitButton.setOnClickListener(this)

        setFullScreen()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setFullScreen()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancelButton -> {
                dismiss()
            }

            R.id.submitButton -> {
                if (title.text.toString().isBlank() || price.text.toString().isBlank()
                    || amount.text.toString().isBlank()
                ) {
                    Toast.makeText(requireContext(), "There's incorrect input!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    try {
                        val price = parseDouble(price.text.toString())
                        val amount = parseInt(amount.text.toString())
                        val product = Product(amount, price, title.text.toString(), price * amount)

                        this.mListener.onComplete(product);
                        dismiss()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(requireContext(), "Wrong Numeric Format", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    interface OnCompleteListener {
        fun onComplete(product: Product)
    }

    private fun DialogFragment.setFullScreen() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}