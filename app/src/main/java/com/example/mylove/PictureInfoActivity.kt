package com.example.mylove

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.palette.graphics.Palette
import com.example.mylove.Adapter.PictureData
import com.example.mylove.databinding.ActivityMainBinding
import com.example.mylove.databinding.ActivityPictureInfoBinding
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt

class PictureInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        val pictureInfo = intent.getParcelableExtra<PictureData>("pictureInfo")

        if (pictureInfo != null) {
            binding.image.setImageURI(pictureInfo.image)
            binding.about.text = pictureInfo.about
        }
    }

    fun ImageView.setImageURI(uri: String?) {
        Log.d("PictureInfoActivity", "URI: $uri")
        if (uri != null && uri.isNotBlank()) {
            setImageURI(Uri.parse(uri))
        }
    }
}