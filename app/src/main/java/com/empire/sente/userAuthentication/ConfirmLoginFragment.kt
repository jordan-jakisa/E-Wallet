package com.empire.sente.userAuthentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.empire.sente.R
import com.empire.sente.databinding.FragmentConfirmLoginBinding
import com.empire.sente.utils.MethodUtils
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmLoginFragment : Fragment() {
    private lateinit var binding: FragmentConfirmLoginBinding
    private val args: ConfirmLoginFragmentArgs by navArgs()
    private lateinit var viewModel: PhoneAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmLoginBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = PhoneAuthViewModel(null, requireActivity())
    }

    private fun init() {
        observeResults()
        binding.sentCodeToSms.text =
            "We've sent an SMS with an activation code to your phone ${args.phoneNumber}"
        binding.resentCodeTv.apply {
            text = MethodUtils.stringBuilder(
                "Didn't get the code? ",
                "Resend",
                requireContext()
            )
            setOnClickListener {
                try {
                    Log.d("PhoneAuthTest", "Phone  number : ${args.phoneNumber}")
                    viewModel.requestPhoneNumberAuth(args.phoneNumber)
                } catch (e: Exception) {
                    Log.d("PhoneAuthTest", "Phone  number exception: $e")

                }
            }
        }
        binding.codeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (p0?.length) {
                    6 -> {
                        binding.verifyButton.setOnClickListener {
                            val credential: PhoneAuthCredential =
                                PhoneAuthProvider.getCredential(
                                    args.verificationId,
                                    p0.toString()
                                )
                            Log.d("PhoneAuthTest", "verifyButton.setOnClickListene")
                            Log.d(
                                "PhoneAuthTest",
                                "ConfirmFragment VerificationId: ${args.verificationId}"
                            )
                            viewModel.signInWithPhoneAuthCredential(credential)
                        }
                    }
                    else -> {
                        binding.verifyButton.setOnClickListener {
                            binding.codeInputLayout.error = "Invalid Code. Please try again."
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun observeResults() {
        viewModel.callbackResult.observe(viewLifecycleOwner) { status ->
            when (status) {
                CallbackResult.COMPLETED -> {
                    findNavController().navigate(R.id.action_global_dashboardFragment)
                }
                CallbackResult.FAILED -> {
                    Toast.makeText(requireContext(), "Verification Failed", Toast.LENGTH_LONG)
                        .show()
                }
                CallbackResult.CODE_SENT -> {
                    Toast.makeText(requireContext(), "Code has been sent", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {}
            }
        }
        viewModel.signInStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                SignInStatus.USER_EXIST -> {
                    findNavController().navigate(R.id.action_global_dashboardFragment)
                }
                SignInStatus.DOES_NOT_EXIST -> {}
                SignInStatus.SET_VALUE_COMPLETE -> {
                    findNavController().navigate(R.id.action_global_dashboardFragment)
                }
                SignInStatus.SET_VALUE_FAILED -> {
                    Toast.makeText(
                        requireContext(),
                        "Failed to create account",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                SignInStatus.WRONG_CONFIRMATION_CODE -> {
                    binding.codeInputLayout.error = "Wrong Confirmation Code"
                }
                else -> {}
            }
        }
    }

}