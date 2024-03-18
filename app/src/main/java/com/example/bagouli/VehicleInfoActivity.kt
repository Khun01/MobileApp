package com.example.bagouli

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bagouli.Data.Cars
import com.example.bagouli.databinding.ActivityVehicleInfoBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class VehicleInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cd1.setOnClickListener {
            onBackPressed()
        }
        binding.rnBTN.setOnClickListener {
            val intent = Intent(this, RentVehicleActivity::class.java)
            startActivity(intent)
        }
        getCarsInfo()
        val progress = binding.ratings.text.toString().toFloatOrNull()
        binding.progressStar.rating = progress!!
    }


    private fun getCarsInfo(){
        val car = intent.getSerializableExtra("cars") as Cars

        Picasso.get()
            .load(car.image)
            .into(binding.image)
        binding.brand.text = car.brand
        binding.name.text = car.model
        binding.price.text = car.price_per_day.toString()
        binding.about.text = car.description
        binding.ratings.text = car.stars.toString()
        binding.maxHorsePower.text = car.horsepower
        binding.topSpeed.text = car.top_speed
        binding.acceleration.text = car.acceleration
        binding.overlay.setBackgroundColor(Color.parseColor("#40000000"))

        binding.rnBTN.setOnClickListener {
            val intent = Intent(this, RentVehicleActivity::class.java)
            intent.putExtra("price", binding.price.text.toString())
            startActivity(intent)
            finish()
        }

    }
    private fun compressBitmap(bitmap: Bitmap, quality: Int, maxSizeKb: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        var sizeKb = outputStream.toByteArray().size / 1024
        var scale = 1f
        while (sizeKb > maxSizeKb){
            scale *= 0.9f
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            outputStream.reset()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            sizeKb = outputStream.toByteArray().size / 1024
        }
        return BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
    }
    private fun saveBitmapToFile(bitmap: Bitmap): File?{
        val file = File(this.externalCacheDir, "image.png")
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
}