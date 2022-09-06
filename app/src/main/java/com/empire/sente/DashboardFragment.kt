package com.empire.sente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.empire.sente.databinding.FragmentDashboardBinding
import com.empire.sente.utils.FirebaseUtils.firebaseAuth

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.logoutButton.setOnClickListener {
            firebaseAuth().signOut()
            findNavController().navigate(R.id.action_global_phoneFragment)
        }
    }
}