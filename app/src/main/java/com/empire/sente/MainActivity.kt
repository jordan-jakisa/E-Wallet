package com.empire.sente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.empire.sente.utils.FirebaseUtils.firebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth().currentUser != null) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_dashboardFragment)
        } else {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_phoneFragment)
        }
    }
}