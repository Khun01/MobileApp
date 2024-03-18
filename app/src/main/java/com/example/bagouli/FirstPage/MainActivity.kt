package com.example.bagouli.FirstPage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bagouli.R
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.UserPreferences
import com.example.bagouli.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation()
        handleIntentData()
        val token = UserPreferences.getToken(this)
        if (token != null) {
            APIClient.setAuthToken(token)
            navController.navigate(R.id.homeFragment)
        }
    }

    private fun handleIntentData(){
        if (Intent.ACTION_VIEW == intent.action){
            val uri : Uri? = intent.data
            uri?.let {
                val token = uri.getQueryParameter("token")
                if (!token.isNullOrEmpty()){
                    val bundle = Bundle()
                    bundle.putString("token", token)
                    navController.navigate(R.id.resetPasswordFragment, bundle)
                }
            }
        }
    }

    private fun navigation(){
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.registerFragment || nd.id == R.id.loginFragment || nd.id == R.id.landingFragment ||
                nd.id == R.id.privacyPolicyFragment || nd.id == R.id.mapFragment || nd.id == R.id.editProfileFragment ||
                nd.id == R.id.forgotPasswordFragment || nd.id == R.id.resetPasswordFragment){
                binding.bottomNav.visibility = View.GONE
            }else{
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }
}