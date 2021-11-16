package com.paviotti.s2.data.remote.auth

import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paviotti.s2.data.model.User
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

/**
 * Orerações realizadas aqui:
 * autenticação no FirebaseAuth (signIn);
 *
 * criação de um usuário no FrebaseAuth (signUp);
 *
 * gravação de uma foto no FirebaseStorage usando como referência o ID do usuário Auth (updateUserProfile);
 * Comando putBytes()
 * */

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
              //  .set(User(email, username, "photo_url.PNG a ser inserido depois")).await()
                .set(User(email, username)).await()
        }
        return authResult.user
    }

    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/26519106#overview
    /**não retorna nada porque ela atualiza o Firebase com o nome e a foto do usuário*/
    suspend fun updateUserProfile(imageBitmap: Bitmap, username: String) {
        /** pegar o usuario*/
        val user = FirebaseAuth.getInstance().currentUser

        /** em FirebaseStorage, criar uma pasta user.uid/profile_picture */
        val imagRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/profile_picture")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        /** sobe a foto para o Firebase e pega a URL da imagRef*/
        val downloadUrl =
            imagRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()

        /** https://firebase.google.com/docs/auth/android/manage-users#kotlin+ktx*/
        val profileUpdates = userProfileChangeRequest {
            displayName = username //"Roberto"
            photoUri = Uri.parse(downloadUrl)
        }
        user?.updateProfile(profileUpdates)?.await() //atualiza os dados

        //salva o link da imagem no Firestore
        user?.let {
            FirebaseFirestore.getInstance().collection("users").document(it?.uid)
                .update("photo_url", downloadUrl, "username", username, "user_id", it.uid)
        }
    }

    suspend fun findImage(): String {
        var img = ""
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            val urlImage =
                FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                img = urlImage.get("photo_url") as String
        }
        return img
    }
}