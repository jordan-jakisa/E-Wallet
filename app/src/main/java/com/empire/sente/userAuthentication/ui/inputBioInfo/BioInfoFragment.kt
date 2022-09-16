package com.empire.sente.userAuthentication.ui.inputBioInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.empire.sente.databinding.FragmentBioInfoBinding
import com.empire.sente.userAuthentication.Utils.TextWatchers.dayWatcher
import com.empire.sente.userAuthentication.Utils.TextWatchers.monthWatcher
import com.empire.sente.userAuthentication.Utils.TextWatchers.yearWatcher
import com.empire.sente.userAuthentication.models.User
import com.empire.sente.userAuthentication.ui.phoneInput.SignInStatus
import com.empire.sente.utils.FirebaseUtils.firebaseAuth

class BioInfoFragment : Fragment() {
    private lateinit var binding: FragmentBioInfoBinding
    private val args: BioInfoFragmentArgs by navArgs()
    private val viewModel: BioInfoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBioInfoBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        monthWatcher(binding.monthInputEt)
        dayWatcher(binding.dayInputEt)
        yearWatcher(binding.yearInputEt)
        binding.nextButton.setOnClickListener {
            val firstName = binding.firstNameInput.text?.trim()
            val lastName = binding.secondNameInput.text?.trim()
            val birthMonth = binding.monthInputEt.text?.trim()
            val birthDay = binding.dayInputEt.text?.trim()
            val birthYear = binding.yearInputEt.text?.trim()
            val gender = binding.genderInputEt.text?.trim()

            if (firstName?.isNotEmpty() == true
                && lastName?.isNotEmpty() == true
                && birthMonth?.isNotEmpty() == true
                && birthDay?.isNotEmpty() == true
                && birthYear?.isNotEmpty() == true
                && gender?.isNotEmpty() == true
            ) {
                val uid = firebaseAuth().currentUser?.uid
                val user = User(
                    uid,
                    firstName.toString(),
                    lastName.toString(),
                    birthMonth.toString(),
                    birthDay.toString(),
                    birthYear.toString(),
                    gender.toString(),
                    args.phoneNumber,
                    "0",
                    ""
                )
                if (uid != null) {
                    viewModel.createUserInRTD(uid, user)
                    observeViewModel()
                }
            } else if (firstName?.isEmpty() == true) {
                binding.firstNameInputLayout.error = "Required"
            } else if (lastName?.isEmpty() == true) {
                binding.secondNameInputLayout.error = "Required"
            } else if (birthMonth?.isEmpty() == true) {
                binding.monthInputLayout.error = "Required"
            } else if (birthDay?.isEmpty() == true) {
                binding.dayInputLayout.error = "Required"
            } else if (birthYear?.isEmpty() == true) {
                binding.yearInputLayout.error = "Required"
            } else if (gender?.isEmpty() == true) {
                binding.genderInputLayout.error = "Required"
            }

        }
    }

    private fun observeViewModel() {
        viewModel.signInStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                SignInStatus.SUCCESS -> findNavController().navigate(BioInfoFragmentDirections.actionGlobalDashboardFragment())
                SignInStatus.FAILED -> Toast.makeText(
                    requireContext(),
                    "Failed. Please retry",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {}
            }
        }
    }
}