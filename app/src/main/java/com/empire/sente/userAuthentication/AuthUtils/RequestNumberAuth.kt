package com.empire.sente.userAuthentication.AuthUtils

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.navArgs
import com.empire.sente.userAuthentication.PhoneFragment
import com.empire.sente.userAuthentication.PhoneFragmentArgs
import com.empire.sente.userAuthentication.PhoneFragmentDirections
import com.empire.sente.utils.FirebaseUtils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

object RequestNumberAuth {
    fun requestPhoneNumberAuth(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        Log.d(PhoneFragment.TAG, "Phone Number: $phoneNumber")
        val options = PhoneAuthOptions.newBuilder(FirebaseUtils.firebaseAuth())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}