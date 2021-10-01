package com.paviotti.s2.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser
import com.paviotti.s2.data.remote.auth.AuthDataSource

class AuthRepositoryImplement(private val dataSource: AuthDataSource) : AuthRepository {
    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        return dataSource.signIn(email, password)
    }

    override suspend fun signUn(email: String, password: String, username: String): FirebaseUser? =
        dataSource.signUp(email, password, username) //chama AuthDataSource

    //este método chama dataSource.updateUserProfile() em AuthDataSource.kt passando a foto e o nome
    override suspend fun updateProfile(imageBitmap: Bitmap, username: String) =
        dataSource.updateUserProfile(imageBitmap, username)

}