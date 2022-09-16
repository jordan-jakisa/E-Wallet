package com.empire.sente.userAuthentication.ui.phoneInput

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.empire.sente.R
import com.empire.sente.databinding.FragmentPhoneBinding
import com.empire.sente.utils.FirebaseUtils.firebaseAuth
import com.empire.sente.utils.fadeIn
import com.empire.sente.utils.fadeOut
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneFragment : Fragment() {
    private lateinit var binding: FragmentPhoneBinding
    private lateinit var phoneNumber: String
    private val viewModel: PhoneAuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.phoneInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (p0?.length) {
                    9 -> {
                        binding.confirmButton.setOnClickListener {
                            binding.sendingDialogView.fadeIn(500)
                            phoneNumber = "+256$p0"
                            requestPhoneNumberAuth(phoneNumber)
                            observeViewModel()
                        }
                    }
                    else -> {
                        binding.confirmButton.setOnClickListener {
                            binding.phoneInputLayout.error =
                                context?.getString(R.string.phone_number_condition)
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.confirmButton.setOnClickListener {
                    binding.phoneInputLayout.error =
                        context?.getString(R.string.phone_number_condition)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    fun observeViewModel() {
        viewModel.callbackResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                CallbackResult.COMPLETED -> binding.sendingDialogView.fadeOut(500, 500)
                CallbackResult.FAILED -> {
                    binding.sendingDialogView.fadeOut(500, 500)
                    binding.phoneInputLayout.error =
                        context?.getString(R.string.error_requesting_code)
                }
                CallbackResult.CODE_SENT -> binding.sendingDialogView.fadeOut(500, 500)
                else -> {}
            }
        }

        viewModel.credential.observe(viewLifecycleOwner) { credential ->
            viewModel.checkIfUserExistsInDb(phoneNumber, credential)
        }

        viewModel.signInStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                SignInStatus.SUCCESS -> findNavController().navigate(PhoneFragmentDirections.actionPhoneFragmentToDashboardFragment())
                SignInStatus.DOES_NOT_EXIST -> findNavController().navigate(
                    PhoneFragmentDirections.actionPhoneFragmentToBioInfoFragment(
                        phoneNumber
                    )
                )
                else -> {}
            }
        }

        viewModel.verificationDetails.observe(viewLifecycleOwner) { verificationDetails ->
            when {
                verificationDetails != null -> {
                    findNavController().navigate(
                        PhoneFragmentDirections.actionPhoneFragmentToConfirmLoginFragment(
                            verificationDetails.verificationId,
                            verificationDetails.token,
                            phoneNumber
                        )
                    )
                }
            }
        }
    }

    fun requestPhoneNumberAuth(
        phoneNumber: String
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(viewModel.callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}