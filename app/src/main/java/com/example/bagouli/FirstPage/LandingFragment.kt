package com.example.bagouli.FirstPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bagouli.R
import com.example.bagouli.databinding.FragmentLandingBinding

class LandingFragment : Fragment() {
    private lateinit var binding: FragmentLandingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentLandingBinding.inflate(inflater,container,false)
        binding.loginBTN.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
        }
        binding.registerBTN.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_registerFragment)
        }
        return binding.root
    }
}