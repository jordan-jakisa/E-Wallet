package com.empire.sente.userAuthentication.ui.phoneInput

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.empire.sente.userAuthentication.models.User
import com.empire.sente.utils.FIREBASE_USERS_NODE
import com.empire.sente.utils.FirebaseUtils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

enum class CallbackResult {
    COMPLETED, FAILED, CODE_SENT
}

enum class SignInStatus {
    SUCCESS, FAILED, DOES_NOT_EXIST
}

class VerificationDetails(
    val verificationId: String,
    val token: PhoneAuthProvider.ForceResendingToken
)

class PhoneAuthViewModel : ViewModel() {

    private var _callbackResult = MutableLiveData<CallbackResult>()
    val callbackResult: LiveData<CallbackResult> get() = _callbackResult

    private var _signInStatus = MutableLiveData<SignInStatus>()
    val signInStatus: LiveData<SignInStatus> get() = _signInStatus

    private var _credential = MutableLiveData<PhoneAuthCredential>()
    val credential: LiveData<PhoneAuthCredential> get() = _credential

    private var _verificationDetails = MutableLiveData<VerificationDetails>()
    val verificationDetails: LiveData<VerificationDetails> get() = _verificationDetails

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            _credential.value = credential
            _callbackResult.value = CallbackResult.COMPLETED
            Log.d("PhoneAuthTest", "onVerificationCompleted")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _callbackResult.value = CallbackResult.FAILED

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d("PhoneAuthTest", "onVerificationFailed Error: $e")

            } else if (e is FirebaseTooManyRequestsException) {
                Log.d("PhoneAuthTest", "onVerificationFailed Error: $e")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            val verificationDetails = VerificationDetails(verificationId, token)
            _verificationDetails.value = verificationDetails
        }
    }

    fun checkIfUserExistsInDb(phoneNumber: String, credential: PhoneAuthCredential) {
        FirebaseUtils.firebaseDatabase().child(FIREBASE_USERS_NODE)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val user = ds.getValue(User::class.java)
                        if (user != null) {
                            when (user.phoneNumber) {
                                phoneNumber -> {
                                    FirebaseUtils.firebaseAuth().signInWithCredential(credential)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "PhoneAUth",
                                                "User exists and Sign in with credential successful"
                                            )
                                            _signInStatus.value = SignInStatus.SUCCESS
                                        }.addOnFailureListener {
                                        Log.d(
                                            "PhoneAUth",
                                            "User exists and Sign in with credential un-successful"
                                        )
                                        _signInStatus.value = SignInStatus.FAILED
                                    }
                                }
                                else -> {
                                    _signInStatus.value = SignInStatus.DOES_NOT_EXIST
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("PhoneAUth", "Database Error: $error")
                }

            })
    }

}