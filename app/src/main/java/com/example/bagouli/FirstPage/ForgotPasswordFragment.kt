package com.example.bagouli.FirstPage

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import com.example.bagouli.Data.ForgotPasswordRequest
import com.example.bagouli.DataModels.ForgotPasswordResponse
import com.example.bagouli.R
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.databinding.FragmentForgotPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        binding.cd1.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.resetBTN.setOnClickListener {
            val email = binding.email.text.toString()
            if (email.isNotEmpty()){
                forgotPassword(email)
            }else{
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private fun forgotPassword(email: String){
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Sending reset link ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val call = APIClient.getService().forgotPassword(ForgotPasswordRequest(email))
        call.enqueue(object : Callback<ForgotPasswordResponse>{
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful){
                    Toast.makeText(context, "You can now reset your password", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                }else{
                    Toast.makeText(context, "Failed to send link", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}