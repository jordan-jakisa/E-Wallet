package com.empire.sente.userAuthentication.Utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

object TextWatchers {
    fun monthWatcher(view: TextInputEditText) {
        view.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != ""){
                    if (p0.toString().toInt() < 1){
                        view.error = "Badly formatted month"
                    }
                    if (p0.toString().toInt() > 12){
                        view.error = "Badly formatted month"
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    fun dayWatcher(view: TextInputEditText){
        view.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != ""){
                    if (p0.toString().toInt() < 1){
                        view.error = "Badly formatted day"
                    }
                    if (p0.toString().toInt() > 31){
                        view.error = "Badly formatted day"
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }
    fun yearWatcher(view: TextInputEditText){
        view.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != ""){
                    if (p0.toString().toInt() < 1900){
                        view.error = "Badly formatted year"
                    }
                    if (p0.toString().toInt() > 2022){
                        view.error = "Badly formatted year"
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

}