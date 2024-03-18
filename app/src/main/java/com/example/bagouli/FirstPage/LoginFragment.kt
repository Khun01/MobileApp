package com.example.bagouli.FirstPage

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bagouli.Data.LoginRequest
import com.example.bagouli.DataModels.LoginResponse
import com.example.bagouli.R
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.UserPreferences
import com.example.bagouli.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(), View.OnFocusChangeListener {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        binding.registerBTN.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        binding.loginBTN.setOnClickListener {
            val imm =context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(context, "Please put your info", Toast.LENGTH_SHORT).show()
            }else{
                login(email, password)
            }
        }
        return binding.root
    }

    private fun login(email: String, password: String){
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Logging in...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val loginRequest = LoginRequest(email, password)
        val call = APIClient.getService().login(loginRequest)
        call.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse != null){
                        UserPreferences.saveUserData(requireContext(), loginResponse)
                        APIClient.setAuthToken(loginResponse.token)
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                        Log.e("User", "User Info: ${loginResponse.user}")
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        progressDialog.dismiss()
                    }else{
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(context, "These credentials do not match our records", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validateEmail(): Boolean {
        var errorMessage: String? = null
        val value = binding.email.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email address is invalid"
        }
        if (errorMessage != null) {
            binding.til1.apply {
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
            binding.til2.apply {
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
                        if (binding.til1.isErrorEnabled){
                            binding.til1.isErrorEnabled = false
                        }
                    }else{
                        validateEmail()
                    }
                }
                R.id.password -> {
                    if (hasFocus){
                        if (binding.til2.isErrorEnabled){
                            binding.til2.isErrorEnabled = false
                        }
                    }else{
                        validatePassword()
                    }
                }
            }
        }
    }
}