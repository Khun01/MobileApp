package com.example.bagouli

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.bagouli.Data.ReservationRequest
import com.example.bagouli.DataModels.ReservationResponse
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.CarPreferences
import com.example.bagouli.Utils.UserPreferences
import com.example.bagouli.databinding.ActivityRentVehicleBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class RentVehicleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentVehicleBinding
    private lateinit var dialog : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cd1.setOnClickListener {
            onBackPressed()
        }

        binding.dateStart.setOnClickListener {
            datePickerDialog(binding.dateStart)
        }
        binding.dateEnd.setOnClickListener {
            datePickerDialog(binding.dateEnd)
        }

        binding.fullName.text = UserPreferences.getUserInfo(this)!!.name
        binding.email.text = UserPreferences.getUserInfo(this)!!.email

        binding.price.text = intent.getStringExtra("price")

        binding.reservationBTN.setOnClickListener{
            if (binding.fullName.text.isEmpty() || binding.email.text.isEmpty() ||
                binding.dateStart.text.isEmpty() || binding.dateEnd.text.isEmpty() ||
                binding.duration.text.isEmpty() || binding.phoneNumber.text.isEmpty() ||
                binding.paymentMethod.text.isEmpty()){
                Toast.makeText(this, "Please put your information", Toast.LENGTH_SHORT).show()
            }
            else{
                try {
                    val startDateString = binding.dateStart.text.toString()
                    val endDateString = binding.dateEnd.text.toString()
                    val paymentMethod = binding.paymentMethod.text.toString()
                    reserveCar(startDateString, endDateString, paymentMethod)
                }catch (e: ParseException){
                    e.printStackTrace()
                }
            }
        }
        paymentDropdown()
    }
    private fun reserveCar(startDate: String, endDate: String, paymentMethod: String){
        val carId = CarPreferences.getCarId()!!.toInt()
        val reservationRequest = ReservationRequest(startDate, endDate, paymentMethod)
        val call = APIClient.getService().reserveCar(carId, reservationRequest)
        call.enqueue(object : Callback<ReservationResponse> {
            override fun onResponse(call: Call<ReservationResponse>, response: Response<ReservationResponse>) {
                if (response.isSuccessful){
                    val fragment = CheckoutFragment()
                    val enterAnimation = androidx.constraintlayout.widget.R.anim.abc_fade_in
                    val exitAnimation = androidx.transition.R.anim.abc_fade_out
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(enterAnimation, exitAnimation)
                        .add(R.id.container, fragment)
                        .commit()
                }else{
                    Toast.makeText(this@RentVehicleActivity, "Reservation Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                Toast.makeText(this@RentVehicleActivity, "Network Failed, Please try again later.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun datePickerDialog(textView: TextView){
        val calendar = Calendar.getInstance()
        val currentDate = calendar.timeInMillis
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener{ _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEditText(textView, calendar)
                duration()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = currentDate
        datePickerDialog.show()
    }

    private fun updateEditText(textView: TextView, calendar: Calendar) {
        val dataFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(dataFormat, Locale.getDefault())
        textView.text = sdf.format(calendar.time)
    }

    private fun duration() {
        val startDateString = binding.dateStart.text.toString()
        val endDateString = binding.dateEnd.text.toString()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        try {
            val startDate = dateFormat.parse(startDateString)
            val endDate = dateFormat.parse(endDateString)
            val differenceInMills = endDate!!.time - startDate!!.time
            val differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMills)
            binding.duration.text = getString(R.string.days_difference, differenceInDays)

            val pricePerDay = CarPreferences.getCarPrice() ?: 0
            val totalPrice = differenceInDays * pricePerDay
            binding.price.text = totalPrice.toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun paymentDropdown(){
        binding.paymentMethod.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.payment_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when(menuItem.itemId){
                    R.id.item1 -> {
                        binding.paymentMethod.text = "Credit Card"
                        if (binding.paymentMethod.text == "Credit Card"){
                            creditCardDialog()
                        }
                        true
                    }
                    R.id.item2 -> {
                        binding.paymentMethod.text = "Gcash"
                        true
                    }
                    R.id.item3 -> {
                        binding.paymentMethod.text = "Paypal"
                        if (binding.paymentMethod.text == "Paypal"){
                            paypalDialog()
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun paypalDialog(){
        val dialogView = layoutInflater.inflate(R.layout.payment_paypal_dialog, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        val continueBtn = dialogView.findViewById<Button>(R.id.continueBtn)
        val cardNumber = dialogView.findViewById<EditText>(R.id.cardNumber)
        val expirationDate = dialogView.findViewById<EditText>(R.id.expirationDate)
        val securityCode = dialogView.findViewById<EditText>(R.id.securityCode)
        val name = dialogView.findViewById<EditText>(R.id.fullName)
        spacesEditText(dialog, cardNumber)
        expirationDate.setOnClickListener {
            expirationDate(expirationDate)
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        continueBtn.setOnClickListener {
            if (cardNumber.text.isEmpty() && expirationDate.text.isEmpty() && securityCode.text.isEmpty()
                && name.text.isEmpty()){
                Toast.makeText(this, "Please put your number.", Toast.LENGTH_SHORT).show()
            }else{
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun creditCardDialog(){
        val dialogView = layoutInflater.inflate(R.layout.payment_credit_card_dialog, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        val continueBtn = dialogView.findViewById<Button>(R.id.continueBtn)
        val cardNumber = dialogView.findViewById<EditText>(R.id.cardNumber)
        val expirationDate = dialogView.findViewById<EditText>(R.id.expirationDate)
        val securityCode = dialogView.findViewById<EditText>(R.id.securityCode)
        val name = dialogView.findViewById<EditText>(R.id.fullName)
        spacesEditText(dialog, cardNumber)
        expirationDate.setOnClickListener {
            expirationDate(expirationDate)
        }
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        continueBtn.setOnClickListener {
            if (cardNumber.text.isEmpty() && expirationDate.text.isEmpty() && securityCode.text.isEmpty()
                && name.text.isEmpty()){
                Toast.makeText(this, "Please put your number.", Toast.LENGTH_SHORT).show()
            }else{
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun expirationDate(editText: EditText){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, _ ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, selectedYear)
            selectedDate.set(Calendar.MONTH, selectedMonth)
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val formatDate = dateFormat.format(selectedDate.time)
            editText.setText(formatDate)
        },year, month, 0)
        val datePicker = datePickerDialog.datePicker
        datePicker.calendarViewShown = false
        datePicker.spinnersShown = true
        datePickerDialog.show()
    }

    private fun spacesEditText(bottomSheetDialog: BottomSheetDialog, editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) return
                val originalText = s.toString().replace("\\s".toRegex(), "")
                val formattedText = StringBuilder()
                for (i in originalText.indices){
                    formattedText.append(originalText[i])
                    if ((i + 1) % 4 == 0 && i != originalText.length - 1){
                        formattedText.append(" ")
                    }
                }
                editText.removeTextChangedListener(this)
                editText.setText(formattedText.toString())
                editText.setSelection(formattedText.length)
                editText.addTextChangedListener(this)
            }
        })
    }
}