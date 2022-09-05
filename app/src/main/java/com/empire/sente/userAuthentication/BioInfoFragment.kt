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
import androidx.core.widget.addTextChangedListener
import com.empire.sente.databinding.FragmentBioInfoBinding
import com.empire.sente.userAuthentication.Utils.TextWatchers.dayWatcher
import com.empire.sente.userAuthentication.Utils.TextWatchers.monthWatcher
import com.empire.sente.userAuthentication.Utils.TextWatchers.yearWatcher
import com.empire.sente.userAuthentication.models.User
import com.empire.sente.utils.ViewChanger

class BioInfoFragment : Fragment() {
    private lateinit var viewChanger: ViewChanger
    private lateinit var binding: FragmentBioInfoBinding
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
                val user = User(
                    null,
                    firstName.toString(),
                    lastName.toString(),
                    birthMonth.toString(),
                    birthDay.toString(),
                    birthYear.toString(),
                    gender.toString(),
                    ""
                )
                viewChanger.fragmentTransaction(
                    BioInfoFragmentDirections.actionBioInfoFragmentToPhoneFragment(
                        user
                    )
                )
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewChanger = context as ViewChanger
    }

    companion object {
        const val TAG = "BIO_INFO_FRAGMENT"
    }
}