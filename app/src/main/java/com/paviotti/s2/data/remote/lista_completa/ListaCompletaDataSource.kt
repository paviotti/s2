package com.paviotti.s2.data.remote.lista_completa

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.data.model.Produto
import kotlinx.coroutines.tasks.await
import com.paviotti.s2.core.Result
import com.paviotti.s2.ui.listas.lista_completa.ListaCompletaFragment.Companion.nameListFull

class ListaCompletaDataSource {
    /** Esta classe lê a tabela produto completa*/
    suspend fun getLatestListaCompleta(): Result<List<Produto>> {
        val listProdutos = mutableListOf<Produto>()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            /** deve ler os dados em produtos*/
//            val referenceProduct = FirebaseFirestore.getInstance().collection("users").document(uid)
//////                .collection("listas_de_compras")
            val referenceProduct = FirebaseFirestore.getInstance().collection("produtos")
            val querySnapshot = referenceProduct.get().await()
            for (itemList in querySnapshot.documents) {
                itemList.toObject((Produto::class.java))?.let { itens ->
                    // if(itens.descricao =="Lista nova")
                    listProdutos.add(itens) //cria uma lista de Produto
                }
            }
        }
        return Result.Success(listProdutos)
    }

    /** cria um novo item na lista do usuário*/
    suspend fun createNewItem(newItem: Produto) {
        val user = FirebaseAuth.getInstance().currentUser //pega o usuário
        user?.uid?.let { uid ->
            val userReference = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("listas_de_compras")

            //https://firebase.google.com/docs/firestore/query-data/queries?authuser=0
            userReference.whereEqualTo("nome_da_lista", nameListFull)
                .get() //nameListFull vem de companion object
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        var iDList = document.id // ex: 4WfZL6A48BJRbB2ceAbu
                        var idNameList = document.get("nome_da_lista").toString()
                        var btnDelete = document.get("btn_delete").toString()
//                        Log.d(
//                            "Gravando",
//                            "id: ${document.id} => dados: ${document.get("nome_da_lista")}"
//                        )
                        newItem.include_item = true //para mudar a cor do botão
                        userReference.document(iDList).collection(idNameList)
                            .add(newItem)     //collection(idNameList).document().set(newItem).await()

                        Log.d(
                            "Variaveis",
                            "IdList: $iDList idNameList: $idNameList btnDelete: $btnDelete"
                        )
                    }
                }

        }
    }
}
