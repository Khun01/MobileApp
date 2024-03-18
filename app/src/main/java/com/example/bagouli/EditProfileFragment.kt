package com.example.bagouli

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bagouli.databinding.FragmentEditProfileBinding
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.bagouli.Data.ClientRequest
import com.example.bagouli.DataModels.UpdateProfileResponse
import com.example.bagouli.FirstPage.MainActivity
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.UserPreferences
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 123
    private var base64Image: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentEditProfileBinding.inflate(inflater,container,false)
        binding.cd1.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.cd2.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                openImagePicker()
            }else{
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        binding.saveBTN.setOnClickListener {
            if (selectedImageUri != null){
                val name = binding.fullName.text.toString()
                val email = binding.email.text.toString()
                val address = binding.address.text.toString()
                val contactNumber = binding.contactNumber.text.toString()
                val image = base64Image ?: ""
                updateProfile(name, email, address, contactNumber, image)
            }
        }
        return binding.root
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted){
            openImagePicker()
        }else{
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("IntentReset")
    private fun openImagePicker(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            selectedImageUri = data.data
            Log.d("SelectedImageUri", selectedImageUri.toString())
            Picasso.get().load(selectedImageUri).into(binding.profileImage)

            base64Image = selectedImageUri?.let { convertImageTOBase64(it) }
            Log.d("SelectedImageUri", base64Image ?: "Base64 conversion failed")
        }
    }

    private fun decodeBase64Image(base64image: String, outPutPath: String){
        try {
            val decodeBytes = Base64.decode(base64image, Base64.DEFAULT)
            val outputStream = FileOutputStream(outPutPath)
            outputStream.write(decodeBytes)
            outputStream.close()
            Log.e("DecodedImage", "Image decoded and saved to $outPutPath")
        }catch (e:Exception){
            Log.e("DecodedImage", "Error decoding image ${e.message}")
        }
    }

    private fun convertImageTOBase64(imageUri: Uri): String? {
        val inputStream = context?.contentResolver?.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val byteArrayOutputStream =ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun updateProfile(name: String, email: String, address: String, contactNumber: String, image: String){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Updating user info...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val userId = UserPreferences.getUserInfo(requireContext())?.id
        val userUpdate = ClientRequest(name, email, address, contactNumber, image)

        UserPreferences.updateUserEmailAndName(requireContext(), email, name, address, contactNumber)

        val call = APIClient.getService().updateProfile(userId!!, userUpdate)
        call.enqueue(object : Callback<UpdateProfileResponse>{
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful){
                    val updateProfileResponse = response.body()
                    updateProfileResponse?.let {
                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                        image?.let { decodeImage ->
                            val extensions = "jpeg"
                            val filename = "${System.currentTimeMillis()}.$extensions"
                            val outputPath = "/images/avatars/$filename"
                            decodeBase64Image(decodeImage, outputPath)
                        }
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    Log.e("Update Profile", "${response.body()}")
                    Log.e("Update Profile", "${response.code()}")
                    Log.e("Update Profile", response.message())
                    Log.e("Update Profile", "${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("Network Error", "${t.message}")
                Toast.makeText(context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}