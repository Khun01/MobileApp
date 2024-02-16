package com.example.mylove

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.mylove.Adapter.PictureData
import com.example.mylove.Database.PictureDatabaseHelper
import com.example.mylove.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val IMAGE_PICK_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var image : ImageView
    private lateinit var dbHelper: PictureDatabaseHelper
    private var selectedImagePath: String? = null
    private var picture: Bitmap? = null

    private lateinit var calendar: Calendar
    private lateinit var alarmManager :AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        image = ImageView(this)
        dbHelper = PictureDatabaseHelper(this)
        setSupportActionBar(binding.toolbar)

        createNotificationChannel()

        binding.notif.setOnClickListener {
            showDateTimePicker()
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.plusBTN.setOnClickListener {
            showAddPictureDialog()
        }

    }

    private fun showAddPictureDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_picture_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        image = dialog.findViewById(R.id.addPicture)
        val about : EditText = dialog.findViewById(R.id.addAbout)
        val done : TextView = dialog.findViewById(R.id.addDone)
        val cancel : TextView = dialog.findViewById(R.id.addCancel)
        val spinner : Spinner = dialog.findViewById(R.id.`when`)
        val anniversaryOptions = arrayOf("1st Anniv", "2nd Anniv", "3rd Anniv", "4th Anniv")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anniversaryOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        done.setOnClickListener {
            val selectedAnniversaryType = spinner.selectedItem.toString()
            val abouts = about.text.toString()
            if (selectedImagePath != null && abouts.isNotEmpty() && selectedAnniversaryType.isNotEmpty()) {
                val imagePath = getImageUriFromBitmap(this, picture!!)
                val pictureData = PictureData(0, imagePath.toString(), selectedAnniversaryType, abouts)
                val id = dbHelper.addPictures(pictureData)
                if (id > -1){
                    Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this@MainActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImagePath = selectedImageUri?.toString()
            image.setImageURI(selectedImageUri)
            picture = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
        }
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val path  = MediaStore.Images.Media.insertImage(context.contentResolver,bitmap,"File",null)
        return Uri.parse(path.toString())
    }




    // For alarm

    private fun showDateTimePicker() {
        val currentDateTime = Calendar.getInstance()
        val year = currentDateTime.get(Calendar.YEAR)
        val month = currentDateTime.get(Calendar.MONTH)
        val day = currentDateTime.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }
                showTimePicker(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun setAlarm(timeInMillis: Long) {
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        Toast.makeText(this,"Alarm set successfully", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker(selectedDate : Calendar) {
        val hour = selectedDate.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDate.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                selectedDate.apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                setAlarm(selectedDate.timeInMillis)
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name : CharSequence = "MyChannel"
            val channel = NotificationChannel("Anniversary", name, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }
}