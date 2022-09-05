package com.empire.sente.userAuthentication

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.empire.sente.MainActivity
import com.empire.sente.R
import com.empire.sente.databinding.FragmentPhoneBinding
import com.empire.sente.userAuthentication.AuthUtils.RequestNumberAuth.requestPhoneNumberAuth
import com.empire.sente.userAuthentication.models.User
import com.empire.sente.utils.FirebaseUtils.firebaseAuth
import com.empire.sente.utils.FirebaseUtils.firebaseDatabase
import com.empire.sente.utils.MethodUtils.stringBuilder
import com.empire.sente.utils.ViewChanger
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

@AndroidEntryPoint
class PhoneFragment : Fragment() {
    private lateinit var binding: FragmentPhoneBinding
    private lateinit var viewChanger: ViewChanger
    private lateinit var phoneNumber: String
    private val args: PhoneFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.registerTv.text =
            stringBuilder("Do not have an account? ", " Create account", requireContext())

        binding.registerTv.setOnClickListener {
            viewChanger.fragmentTransaction(PhoneFragmentDirections.actionPhoneFragmentToBioInfoFragment())
        }
        binding.phoneInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (p0?.length) {
                    9 -> {
                        binding.confirmButton.setOnClickListener {
                            phoneNumber = "+256$p0"
                            requestPhoneNumberAuth(phoneNumber, requireActivity(), callbacks)
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        binding.confirmButton.setOnClickListener {
                            binding.phoneInputLayout.error = "Phone number must be 9 digits"
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewChanger = context as ViewChanger
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Verification failed, Try again", Toast.LENGTH_SHORT)
                .show()

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.w(TAG, "Invalid request", e)
                Toast.makeText(requireContext(), "Invalid Request: $e", Toast.LENGTH_SHORT).show()


            } else if (e is FirebaseTooManyRequestsException) {
                Log.w(TAG, "The SMS quota for the project has been exceeded", e)
                Toast.makeText(requireContext(), "SMS quota exceeded: $e", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent: $verificationId")
            binding.progressBar.visibility = View.GONE
            val args: PhoneFragmentArgs by navArgs()
            if (args.user != null) {
                viewChanger.fragmentTransaction(
                    PhoneFragmentDirections.actionPhoneFragmentToConfirmLoginFragment(
                        verificationId,
                        phoneNumber,
                        token,
                        args.user
                    )
                )
                Toast.makeText(
                    requireContext(),
                    "User name: ${args.user!!.firstName}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewChanger.fragmentTransaction(
                    PhoneFragmentDirections.actionPhoneFragmentToConfirmLoginFragment(
                        verificationId,
                        phoneNumber,
                        token,
                        null
                    )
                )
                Toast.makeText(requireContext(), "User object is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(
                        requireContext(),
                        "signInWithCredential:success",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = task.result?.user
                    if (user?.uid == firebaseAuth().currentUser?.uid) {
                        viewChanger.globalFragmentTransaction(R.id.action_global_dashboardFragment)
                    } else {
                        if (args.user != null) {
                            val userModel = User(
                                user?.uid,
                                args.user?.firstName,
                                args.user?.lastName,
                                args.user?.birthMonth,
                                args.user?.birthDay,
                                args.user?.birthYear,
                                args.user?.gender,
                                phoneNumber
                            )
                            user?.uid?.let {
                                firebaseDatabase().getReference("Users").child(it)
                                    .setValue(userModel).addOnSuccessListener {
                                        viewChanger.globalFragmentTransaction(R.id.action_global_dashboardFragment)
                                        Toast.makeText(
                                        requireContext(),
                                        "Signed In Successfuly",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error: Failed to sign in",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }

                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
            }
    }

    companion object {
        const val TAG = "Phone_Fragment"
    }
}