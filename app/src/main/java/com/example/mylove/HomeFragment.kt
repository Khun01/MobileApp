package com.example.mylove

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mylove.Adapter.TabAdapter
import com.example.mylove.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewPager.adapter = TabAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "1st"
                1 -> tab.text = "2nd"
                2 -> tab.text = "3rd"
                3 -> tab.text = "4th"
            }
        }.attach()
        return binding.root
    }
}