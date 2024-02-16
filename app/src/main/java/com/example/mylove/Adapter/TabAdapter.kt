package com.example.mylove.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mylove.FirstAnniv
import com.example.mylove.FourthAnniv
import com.example.mylove.SecondAnniv
import com.example.mylove.ThirdAnniv

class TabAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FirstAnniv()
            1 -> SecondAnniv()
            2 -> ThirdAnniv()
            3 -> FourthAnniv()
            else -> FirstAnniv()
        }
    }
}