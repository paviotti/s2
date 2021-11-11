package com.paviotti.s2.data.remote.supermercados

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.model.User
import com.paviotti.s2.data.model.VarStatic.Companion.id_s1
import com.paviotti.s2.data.model.VarStatic.Companion.id_s2
import com.paviotti.s2.data.model.VarStatic.Companion.id_s3
import com.paviotti.s2.data.model.VarStatic.Companion.qteSelect
import com.paviotti.s2.ui.adapter.ListaSupermercadosAdapter.Companion.gravar
import kotlinx.coroutines.tasks.await

/**funcionamento
 * Ao clicar no icone da lista de supermercados, um usuario é incluído nos campos s1, s2, s3 de   "users"
 * Ao desmarcar o usuario é apagado é apagado
 * Se o usuário existir em getLatestListaSupermercado() o supermercado é marcado
 * Permite até 3 clicks
 * */
class ListaSupermercadoDataSource {

//    companion object {
//        var qteSelect = 0
//    }

    suspend fun getLatestListaSupermercado(): Result<List<Supermercado>> {

        var conta = 0
        val listSupermarket = mutableListOf<Supermercado>()
        val user = FirebaseAuth.getInstance().currentUser?.uid


        //aponta para a lista de supermercados
        val referenceSupermarket = FirebaseFirestore.getInstance().collection("supermercados")
        val querySnapshot = referenceSupermarket.get().await()

        //cria a lista de supermercado
        for (itemList in querySnapshot.documents) {
            itemList.toObject(Supermercado::class.java)?.let { itens ->
                //  Log.d("docsup","itemListId: ${itemList.id} itemListData: ${itemList.data} ")
                val referenceSupermarketUser =
                    FirebaseFirestore.getInstance().collection("supermercados")
                        .document(itemList.id).collection("users")
                val querySnapshot2 = referenceSupermarketUser.get().await()
                //recupera os ids selecionados
                for (itemList2 in querySnapshot2.documents) {
                    if (itemList2.getBoolean("selecionado") == true) {
                        itens.selecionado = itemList2.getBoolean("selecionado") as Boolean
                    }
                    Log.d(
                        "qtde",
                        "itemListId2: ${itemList2.id}  selecionado: ${itemList2.getBoolean("selecionado")}  itens.selecionado: ${itens.selecionado} "
                    )
                }
                listSupermarket.add(itens)
            }
        }
        //varre a lista para atulaizar o contador de supermercados selecionados
        for (res in listSupermarket) {
            //soma os itens
            if (res.selecionado) {
                conta++ //soma
            }
        }
        contador(conta) //faz a contagem de supermercados selecionados

      //  Log.d("qtde", "listOriginal: ${contador(conta)} ")
        return Result.Success(listSupermarket)
    }

    /** esta função conta a quantidade de supermercados escolhidos e arqmqzena no Firebase e le de volta*/
    suspend fun contador(qtde: Int): Int {
        val contador = hashMapOf("total" to qtde)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val querySnapshotUser =
                FirebaseFirestore.getInstance().collection("us").document(userId)
            //  FirebaseFirestore.getInstance().collection("us").document(userId).set(contador as Map<String, Any>)
            querySnapshotUser.update(contador as Map<String, Any>).await() //grava o contador
        }

        /**le e devolve o valor do contador*/
        var retornaSoma = 0
        val reference =
            FirebaseFirestore.getInstance().collection("us").get().await()
        for (itens in reference.documents) {
            if (itens.id == userId) {
                // pega a quantidade se supermercados selecionados
                val a = itens.get("total").toString()
                qteSelect = 0
                qteSelect = a.toInt()
                retornaSoma = a.toInt()
            }
        }
        // Log.d("contador", "${qteSelect}")
        return retornaSoma
    }


    /** insere ou exclui a seleção do supermercado da lista */
    suspend fun updateItem(supermercado: Supermercado) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val querySnapshotUser =
                FirebaseFirestore.getInstance().collection("supermercados")
                    .whereEqualTo("id_supermercado", supermercado.id_supermercado).get().await()
            /** Se o uid existir em supermercado/users DELETA, senão insere*/
            for (itemId in querySnapshotUser.documents) {
                val dados = hashMapOf("uid" to uid, "selecionado" to true)

                /** pesquisa em supermercado/document(?)/users/document(uid)
                 * grava o id do cliente no supermercado */
                val query2 = FirebaseFirestore.getInstance().collection("supermercados")
                    .document(supermercado.id_supermercado)
                    .collection("users").whereEqualTo("uid", uid).get().await()
                if (itemId.get("id_supermercado") == supermercado.id_supermercado) {
                    //insert
                    //se recebe falso INSERE O REGISTRO
                    if (gravar == true) {
                        // if (supermercado.selecionado == true) {
                        FirebaseFirestore.getInstance().collection("supermercados")
                            .document(itemId.id)
                            .collection("users").add(dados).await()
                    } else {
                        // Log.d("qtde", "entrei aqui")
                        for (iduser in query2.documents) {
                            if (iduser.get("uid") == uid) {
                                //  Log.d("qtde", "uid:$uid  item2: ${iduser.id} DELETE")

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
                    //   Log.d("insertUser", "IdExistente: $supermercado.i")
                }
            }
        }
    }


}