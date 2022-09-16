package com.empire.sente.userAuthentication.ui.confirmCode

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.empire.sente.R
import com.empire.sente.databinding.FragmentConfirmLoginBinding
import com.empire.sente.userAuthentication.ui.phoneInput.CallbackResult
import com.empire.sente.userAuthentication.ui.phoneInput.SignInStatus
import com.empire.sente.utils.FirebaseUtils
import com.empire.sente.utils.MethodUtils
import com.empire.sente.utils.fadeIn
import com.empire.sente.utils.fadeOut
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ConfirmLoginFragment : Fragment() {
    private lateinit var binding: FragmentConfirmLoginBinding
    private val args: ConfirmLoginFragmentArgs by navArgs()
    private val viewModel: ConfirmLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmLoginBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.sentCodeToSms.text = context?.getString(R.string.have_sent_code, args.phoneNumber)
        binding.resentCodeTv.apply {
            text = MethodUtils.stringBuilder(
                context.getString(R.string.did_not_get_code),
                context.getString(R.string.resend),
                requireContext()
            )
            setOnClickListener {
                try {
                    Log.d("PhoneAuthTest", "Phone  number : ${args.phoneNumber}")
                    requestPhoneNumberAuth(args.phoneNumber)
                    binding.sendingDialogView.fadeIn(500)
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
                            binding.sendingDialogView.fadeIn(500)
                            val credential: PhoneAuthCredential =
                                PhoneAuthProvider.getCredential(args.verificationId, p0.toString())
                            viewModel.signInWithCredential(credential, args.phoneNumber)
                            observeViewModel()
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

    private fun observeViewModel() {
        viewModel.authStatus.observe(viewLifecycleOwner) { authStatus ->
            when (authStatus) {
                AuthStatus.SUCCESS_TO_BIO -> findNavController().navigate(
                    ConfirmLoginFragmentDirections.actionConfirmLoginFragmentToBioInfoFragment(args.phoneNumber)
                )
                AuthStatus.SUCCESS_TO_DASH -> findNavController().navigate(R.id.action_global_dashboardFragment)
                else -> {}
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            when {
                error.isNotEmpty() -> {
                    binding.sendingDialogView.fadeOut(500, 500)
                    binding.codeInputLayout.error = error
                }
            }
        }

        viewModel.signInStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                SignInStatus.SUCCESS -> findNavController().navigate(R.id.action_global_dashboardFragment)
                SignInStatus.DOES_NOT_EXIST -> findNavController().navigate(
                    ConfirmLoginFragmentDirections.actionConfirmLoginFragmentToBioInfoFragment(args.phoneNumber)
                )
                else -> {}
            }
        }

        viewModel.callbackResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                CallbackResult.COMPLETED -> binding.sendingDialogView.fadeOut(500, 500)
                CallbackResult.FAILED -> {
                    binding.sendingDialogView.fadeOut(500, 500)
                    binding.codeInputLayout.error =
                        context?.getString(R.string.error_requesting_code)
                }
                CallbackResult.CODE_SENT -> binding.sendingDialogView.fadeOut(500, 500)
                else -> {}
            }
        }

        viewModel.credential.observe(viewLifecycleOwner) { credential ->
            viewModel.checkIfUserExistsInDb(args.phoneNumber, credential)
        }

        viewModel.verificationDetails.observe(viewLifecycleOwner) { verificationDetails ->
            when {
                verificationDetails != null -> {
                    binding.sendingDialogView.fadeOut(500, 500)
                    binding.sentCodeToSms.text = context?.getString(R.string.code_has_been_resent)
                }
            }

        }
    }

    private fun requestPhoneNumberAuth(
        phoneNumber: String
    ) {
        val options = PhoneAuthOptions.newBuilder(FirebaseUtils.firebaseAuth())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(viewModel.callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}