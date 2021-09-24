package com.paviotti.s2.domain.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): FirebaseUser?
}