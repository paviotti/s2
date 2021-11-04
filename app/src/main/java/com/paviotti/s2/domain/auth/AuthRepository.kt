package com.paviotti.s2.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    /** esta interface existe para obrigar a instanciar o metodo em AuthRepositoryimplement*/
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signUn(email: String, password: String, username: String):FirebaseUser?

    //passa uma foto e o nome do usuário e será implementado
    suspend fun updateProfile(imageBitmap: Bitmap, username: String )

    //le a foto do perfil no Firestore e devolve para SetupProfileFragment
    suspend fun findImage():String
}