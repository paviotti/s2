package com.paviotti.s2.domain.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    /** esta interface existe para obrigar a instanciar o metodo em AuthRepositoryimplement*/
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signUn(email: String, password: String, username: String):FirebaseUser?
}