package com.empire.sente.userAuthentication.ui.inputBioInfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.empire.sente.userAuthentication.models.User
import com.empire.sente.userAuthentication.ui.phoneInput.SignInStatus
import com.empire.sente.utils.FIREBASE_USERS_NODE
import com.empire.sente.utils.FirebaseUtils

class BioInfoViewModel : ViewModel() {
    private var _signInStatus = MutableLiveData<SignInStatus>()
    val signInStatus: LiveData<SignInStatus> get() = _signInStatus

    fun createUserInRTD(uid: String, user: User) {
        FirebaseUtils.firebaseDatabase().child(FIREBASE_USERS_NODE)
            .child(uid)
            .setValue(user).addOnSuccessListener {
                Log.d("PhoneAuthTest", "User created in DB")
                _signInStatus.value = SignInStatus.SUCCESS

            }.addOnFailureListener {
                _signInStatus.value = SignInStatus.FAILED
                Log.d(
                    "PhoneAuthTest",
                    "User creation failed in DB Error $it"
                )
            }
    }
}