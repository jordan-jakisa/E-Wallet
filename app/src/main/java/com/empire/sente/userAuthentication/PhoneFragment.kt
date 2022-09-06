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
import com.empire.sente.databinding.FragmentPhoneBinding
import com.empire.sente.utils.MethodUtils.stringBuilder
import com.empire.sente.utils.setVisibility
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneFragment : Fragment() {
    private lateinit var token: PhoneAuthProvider.ForceResendingToken
    private lateinit var verificationId: String
    private lateinit var binding: FragmentPhoneBinding
    private lateinit var phoneNumber: String
    private val args: PhoneFragmentArgs by navArgs()
    private lateinit var viewModel: PhoneAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(layoutInflater)
        viewModel = if (args.user != null) PhoneAuthViewModel(
            args.user,
            requireActivity()
        ) else PhoneAuthViewModel(null, requireActivity())
        init()
        return binding.root
    }

    private fun init() {
        binding.registerTv.apply {
            text =
                stringBuilder("Do not have an account? ", "Create account", requireContext())
            setOnClickListener {
                findNavController().navigate(PhoneFragmentDirections.actionPhoneFragmentToBioInfoFragment())
            }
        }

        binding.phoneInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (p0?.length) {
                    9 -> {
                        binding.confirmButton.setOnClickListener {
                            binding.progressBar.setVisibility(true)
                            phoneNumber = "+256$p0"
                            viewModel.requestPhoneNumberAuth(phoneNumber)
                            observeResults()
                            Log.d("PhoneAuthTest", "confirmButton.setOnClickListener")
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

    private fun observeResults() {
        viewModel.token.observe(viewLifecycleOwner) {
            if (it != null) this.token = it
        }
        viewModel.verificationId.observe(viewLifecycleOwner) {
            if (it != null) this.verificationId = it
            Log.d("PhoneAuthTest", "VerificationId2: $it")
        }
        viewModel.callbackResult.observe(viewLifecycleOwner) { status ->
            when (status) {
                CallbackResult.COMPLETED -> {
                    findNavController().navigate(R.id.action_global_dashboardFragment)
                }
                CallbackResult.FAILED -> {
                    Toast.makeText(requireContext(), "Verification Failed", Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.setVisibility(false)
                }
                CallbackResult.CODE_SENT -> {
                    try {
                        findNavController().navigate(
                            PhoneFragmentDirections.actionPhoneFragmentToConfirmLoginFragment(
                                verificationId,
                                token,
                                phoneNumber
                            )
                        )
                    } catch (e: Exception) {
                        Log.d("PhoneAuthTest", "Exception: $e")
                    }
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
                    Toast.makeText(requireContext(), "User created successfuly", Toast.LENGTH_SHORT)
                        .show()
                }
                SignInStatus.SET_VALUE_FAILED -> {
                    Toast.makeText(requireContext(), "User created successfuly", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {}
            }
        }
    }
}