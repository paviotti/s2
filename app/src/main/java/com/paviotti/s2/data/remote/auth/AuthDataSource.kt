package com.paviotti.s2.data.remote.auth

import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/** Faz o acesso ao Firebase e signIn = autentica, e signUp = cria um novo usuário*/
class AuthDataSource {
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        val authResult =
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user //retorna um usuário do Firebase
    }
}