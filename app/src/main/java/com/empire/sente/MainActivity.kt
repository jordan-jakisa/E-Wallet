package com.empire.sente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.empire.sente.utils.FirebaseUtils.firebaseAuth
import com.empire.sente.utils.ViewChanger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ViewChanger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun fragmentTransaction(action: NavDirections) {
        findNavController(R.id.nav_host_fragment).navigate(action)
    }

    override fun globalFragmentTransaction(actionId: Int) {
        findNavController(R.id.nav_host_fragment).navigate(actionId)
    }


    override fun onStart() {

        super.onStart()
        if (firebaseAuth().currentUser != null){
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_dashboardFragment)
        } else {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_phoneFragment)
        }
    }
}