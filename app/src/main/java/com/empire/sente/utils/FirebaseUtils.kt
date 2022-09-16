package com.empire.sente.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    fun firebaseAuth() = FirebaseAuth.getInstance()
    fun firebaseDatabase() = FirebaseDatabase.getInstance().reference
}