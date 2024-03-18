package com.example.bagouli

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.bagouli.Data.TokenRequest
import com.example.bagouli.FirstPage.MainActivity
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.UserPreferences
import com.example.bagouli.databinding.FragmentAccountBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater,container,false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
        binding.edit.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_editProfileFragment)
        }
        binding.ppf.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_privacyPolicyFragment)
        }
        binding.l7.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_mapFragment)
        }

        binding.name.text = UserPreferences.getUserInfo(requireContext())?.name
        binding.gmailAcc.text = UserPreferences.getUserInfo(requireContext())?.email

        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
        return binding.root
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnYes: TextView = dialog.findViewById(R.id.yes)
        val btnNo: TextView = dialog.findViewById(R.id.no)

        btnYes.setOnClickListener {
            logout()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun logout(){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val token = UserPreferences.getToken(requireContext()).toString()
        val tokenRequest = TokenRequest(token)
        val call = APIClient.getService().logout(tokenRequest)
        call.enqueue(object : Callback<Unit>{
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful){
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    Toast.makeText(context, "Logout Successfully", Toast.LENGTH_SHORT).show()
                    UserPreferences.clearUserData(requireContext())
                    progressDialog.dismiss()
                }else{
                    progressDialog.dismiss()
                    Log.e("Logout Failed", response.code().toString())
                    Toast.makeText(context, "Logout Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Network Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showExitDialog(){
        AlertDialog.Builder(context)
            .setTitle("Exit Confirmation")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit"){ dialog, which ->
                requireActivity().finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}