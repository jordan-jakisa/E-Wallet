package com.empire.sente

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.empire.sente.databinding.FragmentDashboardBinding
import com.empire.sente.utils.FirebaseUtils.firebaseAuth
import com.empire.sente.utils.ViewChanger

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewChanger: ViewChanger
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewChanger = context as ViewChanger
    }

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
            viewChanger.globalFragmentTransaction(R.id.action_global_phoneFragment)
        }
    }
}