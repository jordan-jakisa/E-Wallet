package com.empire.sente.userAuthentication

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empire.sente.userAuthentication.models.User
import com.empire.sente.utils.FirebaseUtils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class CallbackResult {
    COMPLETED, FAILED, CODE_SENT
}

enum class SignInStatus {
    USER_EXIST, DOES_NOT_EXIST, SET_VALUE_COMPLETE, SET_VALUE_FAILED, WRONG_CONFIRMATION_CODE

}

class PhoneAuthViewModel(val user: User?, val activity: Activity) : ViewModel() {
    private var _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> get() = _verificationId

    private var _token = MutableLiveData<PhoneAuthProvider.ForceResendingToken>()
    val token: LiveData<PhoneAuthProvider.ForceResendingToken> get() = _token

    private var _callbackResult = MutableLiveData<CallbackResult>()
    val callbackResult: LiveData<CallbackResult> get() = _callbackResult

    private var _signInStatus = MutableLiveData<SignInStatus>()
    val signInStatus: LiveData<SignInStatus> get() = _signInStatus

    private var _phoneNumber = MutableLiveData<String>()
    private val phoneNumber: LiveData<String> get() = _phoneNumber

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            _callbackResult.value = CallbackResult.COMPLETED
            signInWithPhoneAuthCredential(credential)
            Log.d("PhoneAuthTest", "onVerificationCompleted")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _callbackResult.value = CallbackResult.FAILED
            Log.d("PhoneAuthTest", "onVerificationFailed")


            if (e is FirebaseAuthInvalidCredentialsException) {

            } else if (e is FirebaseTooManyRequestsException) {
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            _verificationId.value = verificationId
            _token.value = token
            _callbackResult.value = CallbackResult.CODE_SENT
        }
    }

    fun requestPhoneNumberAuth(
        phoneNumber: String
    ) {
        _phoneNumber.value = phoneNumber
        Log.d("PhoneAuthTest", "PhoneNumberVM: $phoneNumber")
        viewModelScope.launch {
            val options = PhoneAuthOptions.newBuilder(FirebaseUtils.firebaseAuth())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            FirebaseUtils.firebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val currentUser = task.result?.user
                        if (currentUser?.uid == FirebaseUtils.firebaseAuth().currentUser?.uid) {
                            _signInStatus.value = SignInStatus.USER_EXIST
                        } else {
                            _signInStatus.value = SignInStatus.DOES_NOT_EXIST
                            if (user != null) {
                                val userModel = User(
                                    currentUser?.uid, user.firstName, user.lastName,
                                    user.birthMonth,
                                    user.birthDay,
                                    user.birthYear,
                                    user.gender,
                                    phoneNumber.toString()
                                )
                                if (currentUser?.uid != null) {
                                    FirebaseUtils.firebaseDatabase().getReference("Users")
                                        .child(currentUser.uid)
                                        .setValue(userModel).addOnSuccessListener {
                                            _signInStatus.value = SignInStatus.SET_VALUE_COMPLETE

                                        }.addOnFailureListener {
                                            _signInStatus.value = SignInStatus.SET_VALUE_FAILED

                                        }
                                }

                            }
                        }

                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            _signInStatus.value = SignInStatus.WRONG_CONFIRMATION_CODE
                        }
                    }
                }
        }
    }
}