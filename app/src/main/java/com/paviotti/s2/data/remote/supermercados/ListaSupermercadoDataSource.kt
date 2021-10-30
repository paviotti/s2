package com.paviotti.s2.data.remote.supermercados

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.model.User
import kotlinx.coroutines.tasks.await

/**funcionamento
 * Ao clicar no icone da lista de supermercados, um usuario é incluído nos campos s1, s2, s3 de   "users"
 * Ao desmarcar o usuario é apagado é apagado
 * Se o usuário existir em getLatestListaSupermercado() o supermercado é marcado
 * Permite até 3 clicks
 * */
class ListaSupermercadoDataSource {
    suspend fun getLatestListaSupermercado(): Result<List<Supermercado>> {
        val listSupermarket = mutableListOf<Supermercado>()
        val user = FirebaseAuth.getInstance().currentUser
        val referenceSupermarket = FirebaseFirestore.getInstance().collection("supermercados")
        val querySnapshot = referenceSupermarket.get().await()
        for (itemList in querySnapshot.documents) {
            itemList.toObject(Supermercado::class.java)?.let { itens ->
                listSupermarket.add(itens)
            }
        }
        return Result.Success(listSupermarket)
    }


    /** insere ou exclui supermercado*/
    suspend fun updateItem(supermercado: Supermercado) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val querySnapshotUser =
                FirebaseFirestore.getInstance().collection("supermercados")
                    .whereEqualTo("id_supermercado", supermercado.id_supermercado).get().await()
            /** Se o uid existir em supermercado/users DELETA, senão insere*/
            for (itemId in querySnapshotUser.documents) {
                val dados = hashMapOf("uid" to uid, "selecionado" to supermercado.selecionado)

                /** pesquisa em supermercado/document(?)/users/document(uid)  */
                val query2 = FirebaseFirestore.getInstance().collection("supermercados")
                    .document(supermercado.id_supermercado)
                    .collection("users").whereEqualTo("uid", uid).get().await()
                if (itemId.get("id_supermercado") == supermercado.id_supermercado) {
                    //insert
                    if (supermercado.selecionado == true) {
                        FirebaseFirestore.getInstance().collection("supermercados")
                            .document(itemId.id)
                            .collection("users").add(dados).await()
                    } else {
                        Log.d("qtde", "entrei aqui")
                        for (iduser in query2.documents) {
                            if (iduser.get("uid")== uid) {
                                Log.d("qtde", "uid:$uid  item2: ${iduser.id} DELETE")

                                //se já existir DELETE e selecionado == false
                                FirebaseFirestore.getInstance().collection("supermercados")
                                    .document(itemId.id)
                                    .collection("users").document(iduser.id).delete().await()
                            }
                        }
                    }

                }
            }

        }
    }

    //deve receber o id do supermercado escolhido

    suspend fun insertUser(supermercado: Supermercado) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            val referenceSupermarket = FirebaseFirestore.getInstance().collection("users")
            // referenceSupermarket.add()
            val querySnapshot = referenceSupermarket.get().await()
            for (itens in querySnapshot.documents) {
                if (itens.id != uid) {
                    Log.d("insertUser", "IdExistente: $supermercado.i")
                }
            }
        }

    }
}