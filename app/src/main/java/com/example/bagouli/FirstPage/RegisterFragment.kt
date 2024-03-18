package com.example.bagouli.FirstPage

import android.app.ProgressDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bagouli.Data.RegisterRequest
import com.example.bagouli.DataModels.RegisterResponse
import com.example.bagouli.R
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener  {
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        binding.fullName.onFocusChangeListener = this
        binding.email.onFocusChangeListener = this
        binding.password.onFocusChangeListener = this
        binding.cPassword.onFocusChangeListener = this

        binding.loginBTN.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.registerBTN.setOnClickListener {
            val imm =context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)

            binding.fullName.clearFocus()
            binding.email.clearFocus()
            binding.password.clearFocus()
            binding.cPassword.clearFocus()

            val name = binding.fullName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val cPassword = binding.cPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || cPassword.isEmpty()){
                Toast.makeText(context, "Please put your info", Toast.LENGTH_SHORT).show()
            }else{
                if (!binding.checkbox.isChecked){
                    Toast.makeText(context, "Please check the checkbox", Toast.LENGTH_SHORT).show()
                }else{
                    if (password == cPassword){
                        register(name, email, password, cPassword)
                    }else{
                        Toast.makeText(context, "Password do not match", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    private fun validateFullName(): Boolean{
        var errorMessage: String? = null
        val value = binding.fullName.text.toString()
        if (value.isEmpty()){
            errorMessage = "Full name is required"
        }
        if (errorMessage != null){
            binding.til1.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
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

    private fun register(name: String, email: String, password: String, cPassword: String){
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val registerRequest = RegisterRequest(name, email, password, cPassword)
        val call = APIClient.getService().register(registerRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful){
                    val registerResponse = response.body()
                    if (registerResponse != null){
                        Toast.makeText(context, registerResponse.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        progressDialog.dismiss()
                    }
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(context, "Email is already taken", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(v: View?) {}

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            when(view.id){
                R.id.fullName -> {
                    if (hasFocus){
                        if (binding.til1.isErrorEnabled){
                            binding.til1.isErrorEnabled = false
                        }
                    }else{
                        validateFullName()
                    }
                }
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

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
}