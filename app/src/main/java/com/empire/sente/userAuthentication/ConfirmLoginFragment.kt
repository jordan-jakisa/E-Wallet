package com.empire.sente.userAuthentication

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.empire.sente.databinding.FragmentConfirmLoginBinding
import com.empire.sente.userAuthentication.AuthUtils.RequestNumberAuth
import com.empire.sente.userAuthentication.AuthUtils.RequestNumberAuth.requestPhoneNumberAuth
import com.empire.sente.utils.MethodUtils
import com.empire.sente.utils.ViewChanger
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import java.nio.channels.Channel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmLoginFragment : Fragment() {
    private lateinit var binding: FragmentConfirmLoginBinding
    private val args : ConfirmLoginFragmentArgs by navArgs()
    private lateinit var viewChanger: ViewChanger


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmLoginBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.resentCodeTv.text = MethodUtils.stringBuilder(
            "Did not get the code? ",
            " Resend",
            requireContext()
        )
        binding.codeInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (p0?.length){
                    6 -> {
                        binding.verifyButton.setOnClickListener {
                            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(args.verificationId, p0.toString())
                            signInWithPhoneAuthCredential(credential)
                        }
                    } else -> {
                    binding.verifyButton.setOnClickListener {
                        binding.codeInputLayout.error = "Wrong Code"
                    }
                }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.resentCodeTv.setOnClickListener {
            requestPhoneNumberAuth(args.phoneNumber, requireActivity(), callbacks)
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(PhoneFragment.TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(PhoneFragment.TAG, "onVerificationFailed", e)
            Toast.makeText(requireContext(), "Verification failed, Try again", Toast.LENGTH_SHORT).show()

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.w(PhoneFragment.TAG, "Invalid request", e)

            } else if (e is FirebaseTooManyRequestsException) {
                Log.w(PhoneFragment.TAG, "The SMS quota for the project has been exceeded", e)
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            // Save verification ID and resending token so we can use them later
//            storedVerificationId = verificationId
//            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(PhoneFragment.TAG, "signInWithCredential:success")
                    //val user = task.result?.user
                    viewChanger.fragmentTransaction(ConfirmLoginFragmentDirections.actionConfirmLoginFragmentToDashboardFragment())
                    //create user in realtime database
                } else {
                    Log.w(PhoneFragment.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewChanger = context as ViewChanger
    }

    companion object {
        const val TAG = "Login Fragment"
    }
}