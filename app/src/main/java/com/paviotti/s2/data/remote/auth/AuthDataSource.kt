package com.paviotti.s2.data.remote.auth

import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.data.model.User
import kotlinx.coroutines.tasks.await

/** Faz o acesso ao Firebase Auth e signIn = autentica, .signInWithEmailAndPassword(email, password) */
class AuthDataSource {
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        val authResult =
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user //retorna um usuário do Firebase
    }

    /** Faz o acesso ao Firebase Auth e signUp = cria um novo usuário .createUserWithEmailAndPassword(email, password)
     * MAS NÃO CRIA USERNAME
     * */
    suspend fun signUp(email: String, password: String, username: String): FirebaseUser? {
        val authResult =
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()

        /**
         * authResult.user?.uid? "uid" é o id do usuario em Auth (gerado acima) que será gravado no Firestore
         * depois cria uma coleção "users" e um documento com "id"(uid) do usuário
         *
         * */
        authResult.user?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    /** aqui é gravado no Firestore os dados do usuario User.kt*/
                .set(User(email, username, "photo_url.PNG a ser inserido depois")).await()
        }

        return authResult.user
    }
}