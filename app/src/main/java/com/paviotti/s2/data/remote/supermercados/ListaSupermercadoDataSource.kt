package com.paviotti.s2.data.remote.supermercados

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.model.VarStatic.Companion.gravar
import com.paviotti.s2.data.model.VarStatic.Companion.qteSupSelect
import kotlinx.coroutines.tasks.await

/**funcionamento da classe
 * Ao clicar no icone da lista de supermercados, um usuario é incluído em supermercados/id/users/id/uid
 * Ao desmarcar o usuario é apagado
 * Se o usuário existir em getLatestListaSupermercado() o supermercado é marcado
 * Permite até 3 clicks
 * */
class ListaSupermercadoDataSource {

    suspend fun getLatestListaSupermercado(): Result<List<Supermercado>> {
        var conta = 0
        val listSupermarket = mutableListOf<Supermercado>()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            /**aponta para a colection supermercados*/
            val referenceSupermarket = FirebaseFirestore.getInstance().collection("supermercados")
            val querySnapshot = referenceSupermarket.get().await()
            //cria a lista de supermercado
            for (itemList in querySnapshot.documents) {
                itemList.toObject(Supermercado::class.java)?.let { itens ->

                    // Log.d("docsup","itemListId: ${itemList.id} itemListData: ${itemList.data} ")
                    //"itens: ${itens.nome_fantasia} itemListData: ${itens.id_supermercado} ")

                    /**aponta para a lista de usuarios de cada supermercado*/
                    val referenceSupermarketUser =
                        FirebaseFirestore.getInstance().collection("supermercados")
                            .document(itemList.id).collection("users")
                    val querySnapshot2 = referenceSupermarketUser.get().await()
                    //recupera os ids selecionados
                    /**Se o usuario atual constar na lista de usuarios do supermercado e
                     * tiver selecionado este supermercado, marca com selecionado */
                    for (itemList2 in querySnapshot2.documents) {
                        if (itemList2.get("uid") == uid && itemList2.getBoolean("selecionado") == true) {
                            itens.selecionado = itemList2.getBoolean("selecionado") as Boolean
                        }
                    }
                    listSupermarket.add(itens)
                }
            }
            //varre a lista para atualizar o contador de supermercados selecionados
            for (res in listSupermarket) {
                //soma os itens
                if (res.selecionado) {
                    conta++ //soma os supermercados selecionados
                  //  Log.d("aqui", "conta: $conta ")
                }
            }
            contador(conta) //faz a contagem de supermercados selecionados

         //   Log.d("qtde", "contador(conta): ${contador(conta)} ")
        }
        return Result.Success(listSupermarket)
    }

    /** esta função conta a quantidade de supermercados escolhidos e armazena no user Firebase e le de volta*/
    suspend fun contador(qtde: Int): Int {
        val contador = hashMapOf("total_sup_selec" to qtde)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val querySnapshotUser =
                FirebaseFirestore.getInstance().collection("users").document(userId)
            querySnapshotUser.update(contador as Map<String, Any>).await() //grava o contador em usuarios
        }

        /** função auxiliar, lê e devolve o valor do contador*/
        var retornaSoma = 0
        val reference =
            FirebaseFirestore.getInstance().collection("users").get().await()
        for (itens in reference.documents) {
            if (itens.id == userId) {
                // pega a quantidade se supermercados selecionados
                val a = itens.get("total_sup_selec").toString()
                qteSupSelect = 0
                qteSupSelect = a.toInt()
                retornaSoma = a.toInt()
            }
        }
     //   Log.d("contador", "${qteSupSelect}")
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
                val dados = hashMapOf("uid" to uid, "selecionado" to true) //grava dados linha 126

                /** pesquisa o usuario em supermercado/document(*)/users/document(uid)
                 * grava o id do usuario no supermercado */
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
                            .collection("users").add(dados).await() //grava os dados
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
        //  getLatestListaSupermercado()
    }

    //deve receber o id do supermercado escolhido
//    suspend fun insertUser(supermercado: Supermercado) {
//        val user = FirebaseAuth.getInstance().currentUser
//        user?.uid?.let { uid ->
//            val referenceSupermarket = FirebaseFirestore.getInstance().collection("users")
//            // referenceSupermarket.add()
//            val querySnapshot = referenceSupermarket.get().await()
//            for (itens in querySnapshot.documents) {
//                if (itens.id != uid) {
//                    //   Log.d("insertUser", "IdExistente: $supermercado.i")
//                }
//            }
//        }
//    }


}