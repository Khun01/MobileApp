package com.example.bagouli.FirstPage

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bagouli.Data.ResetPasswordRequest
import com.example.bagouli.DataModels.ForgotPasswordResponse
import com.example.bagouli.R
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.databinding.FragmentResetPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordFragment : Fragment(), View.OnFocusChangeListener {

    private lateinit var binding: FragmentResetPasswordBinding
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentResetPasswordBinding.inflate(inflater,container,false)

        binding.email.onFocusChangeListener = this
        binding.password.onFocusChangeListener = this
        binding.cPassword.onFocusChangeListener = this

        binding.resetBTN.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val cPassword= binding.cPassword.text.toString()
            if (email.isNotEmpty() || password.isNotEmpty() || cPassword.isNotEmpty()){
                if (password == cPassword){
//                    resetPassword(email, password, cPassword)
                }else{
                    Toast.makeText(context, "Password do not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Please put your new password", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

//    private fun resetPassword(email: String, password: String, cPassword: String){
//        val request = ResetPasswordRequest(email, password, cPassword)
//        val call = APIClient.getService().resetPassword(request)
//        call.enqueue(object : Callback<ForgotPasswordResponse>{
//            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
//                if (response.isSuccessful){
//                    Toast.makeText(context, "Password reset successfully", Toast.LENGTH_SHORT).show()
//                    findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
//                }else{
//                    Toast.makeText(context, "Unable reset password", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
//                Toast.makeText(context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//    private fun extractTokenFromUri(uri: Uri): String? {
//        return uri.getQueryParameter("token")
//    }

    private fun validateEmail(): Boolean{
        var errorMessage: String? = null
        val value = binding.email.text.toString()
        if (value.isEmpty()){
            errorMessage = "Email is required"
        }else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errorMessage = "Email address is invalid"
        }
        if (errorMessage != null){
            binding.til2.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validatePassword(): Boolean{
        var errorMessage: String? = null
        val value = binding.password.text.toString()
        if (value.isEmpty()){
            errorMessage = "Password is required"
        }else if (value.length < 6){
            errorMessage = "Password must be 6 characters long"
        }
        if (errorMessage != null){
            binding.til3.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validateConfirmPassword(): Boolean{
        var errorMessage: String? = null
        val value = binding.cPassword.text.toString()
        if (value.isEmpty()){
            errorMessage = "Confirm password is required"
        }else if (value.length < 6){
            errorMessage = "Confirm password must be 6 characters long"
        }
        if (errorMessage != null){
            binding.til4.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validatePasswordAndConfirmPassword(): Boolean{
        var errorMessage: String? = null
        val password = binding.password.text.toString()
        val cPassword = binding.cPassword.text.toString()
        if (password != cPassword){
            errorMessage = "Confirm password doesn't match with password"
        }
        if (errorMessage != null){
            binding.til4.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            when(view.id){
                R.id.email -> {
                    if (hasFocus){
                        if (binding.til2.isErrorEnabled){
                            binding.til2.isErrorEnabled = false
                        }
                    }else{
                        validateEmail()
                    }
                }
                R.id.password -> {
                    if (hasFocus){
                        if (binding.til3.isErrorEnabled){
                            binding.til3.isErrorEnabled = false
                        }else{
                            binding.til4.apply {
                                setStartIconDrawable(R.drawable.baseline_lock_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.BLACK))
                            }
                            binding.til3.apply {
                                setStartIconDrawable(R.drawable.baseline_lock_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.BLACK))
                            }
                        }
                    }else{
                        if (validatePassword() && binding.cPassword.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordAndConfirmPassword()){
                            if (binding.til4.isErrorEnabled){
                                binding.til4.isErrorEnabled = false
                            }
                            binding.til4.apply {
                                setStartIconDrawable(R.drawable.check_circle_icon)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                            binding.til3.apply {
                                setStartIconDrawable(R.drawable.check_circle_icon)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }else{
                            binding.til4.apply {
                                setStartIconDrawable(R.drawable.baseline_lock_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.BLACK))
                            }
                            binding.til3.apply {
                                setStartIconDrawable(R.drawable.baseline_lock_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.BLACK))
                            }
                        }
                    }
                }
                R.id.cPassword -> {
                    if (hasFocus){
                        if (binding.til4.isErrorEnabled){
                            binding.til4.isErrorEnabled = false
                        }

                    }else{
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()){
                            if (binding.til3.isErrorEnabled){
                                binding.til3.isErrorEnabled = false
                            }
                            binding.til4.apply {
                                setStartIconDrawable(R.drawable.check_circle_icon)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                            binding.til3.apply {
                                setStartIconDrawable(R.drawable.check_circle_icon)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }else{
                            binding.til4.apply {
                                setStartIconDrawable(R.drawable.baseline_lock_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.BLACK))
                            }
                            binding.til3.apply {
                                setStartIconDrawable(R.drawable.baseline_lock_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.BLACK))
                            }
                        }
                    }
                }
            }
        }
    }
}