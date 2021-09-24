package com.paviotti.s2.domain.auth

import com.google.firebase.auth.FirebaseUser
import com.paviotti.s2.data.remote.auth.AuthDataSource

class AuthRepositoryImplement(private val dataSource: AuthDataSource):AuthRepository {
    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        return dataSource.signIn(email, password)
    }
}