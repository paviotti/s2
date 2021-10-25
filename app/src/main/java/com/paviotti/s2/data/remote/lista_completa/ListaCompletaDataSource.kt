package com.paviotti.s2.data.remote.lista_completa

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paviotti.s2.data.model.Produto
import kotlinx.coroutines.tasks.await
import com.paviotti.s2.core.Result
import com.paviotti.s2.ui.adapter.ListaCompletaAdapter
import com.paviotti.s2.ui.listas.lista_completa.ListaCompletaFragment.Companion.nameListFull

class ListaCompletaDataSource {
    companion object {
        var colorIcon: Boolean = false
    }

    /** Esta classe lê a tabela produto completa (principal)*/
    suspend fun getLatestListaCompleta(): Result<List<Produto>> {
        val listProdutos = mutableListOf<Produto>()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            /** deve ler os dados em produtos*/
            /** collection(produto).document()*/
            val referenceProduct = FirebaseFirestore.getInstance().collection("produtos")
            val querySnapshot = referenceProduct.get().await()
            //o for é feito dentro do documento!
            for (itemList in querySnapshot.documents) {
                //transforma itemList na classe Produto()
                itemList.toObject((Produto::class.java))?.let { itens ->
                    itens.id = itemList.id  //grava o id do produto na lista e os dados
                    //==========================================================
                    /** collection(users).document(uid).collection(listas-de_compras)
                     * depois pesquisa no campo ("nome_da_lista", nameListFull) e devolve
                     * or registros nas novas listas criadas
                     *
                     * esta parte da função teria o proposito de pegar o campo (include_item = true)
                     * e devolver para a lista através de
                     * itens.include_item =  doc.getBoolean("include_item") as (Boolean)
                     * o Log.d mostra true, mas o valor não é repassado para lista ainda
                     * */
                    val listReference =
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                            .collection("listas_de_compras")

                    listReference.whereEqualTo(
                        "nome_da_lista", nameListFull
                    ).get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            val iDList = document.id // pega ex: 4WfZL6A48BJRbB2ceAbu = "Lista nova"
                            val idNameList = document.get("nome_da_lista").toString()
                            listReference.document(iDList).collection(idNameList).get()
                                .addOnSuccessListener { docs ->
                                    for (doc in docs) {

                                        if (doc.get("id").toString() == itemList.id) {
                                            /** precisa chegar true até a lista para mudar a cor do botão*/
                                            itens.include_item =
                                                doc.getBoolean("include_item") as (Boolean)
                                            itens.quantidade =
                                                doc.get("quantidade") as Double //repassa o estoque para o banco
                                            // Log.d("itens", "Qtde ${itens.quantidade}")
//                                            Log.d("Itens", "doc.get: ${doc.get("photo_url")}")
//                                            Log.d("Itens", "itens.photo_url: ${itens.photo_url}")

                                        }

                                    }

                                }
                        }
                    }

                    listProdutos.add(itens) //cria uma lista de Produto
                    // listProdutos.last()
                }
            }
        }
        return Result.Success(listProdutos)
    }

    //==========================================================
    /** cria um novo item na lista do usuário*/
    suspend fun createNewItem(newItem: Produto) {
        lateinit var iDList: String
        lateinit var idNameList: String
        var exist = false
        val user = FirebaseAuth.getInstance().currentUser //pega o usuário
        user?.uid?.let { uid ->
            val listReference = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("listas_de_compras")
            //https://firebase.google.com/docs/firestore/query-data/queries?authuser=0
            listReference.whereEqualTo(
                "nome_da_lista",
                nameListFull
            )//nome da lista pego em companion
                .get() //nameListFull vem de companion object
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        iDList = document.id // pega ex: 4WfZL6A48BJRbB2ceAbu = "Lista nova"
                        idNameList = document.get("nome_da_lista").toString()
                        //   Log.d(
//                            "Gravando",
//                            "id: ${document.id} => dados: ${document.get("nome_da_lista")}"
//                        )
//                        Log.d("lista", "idlista:  $iDList, nomeLista: $idNameList ")
//                        Log.d("Ittens", "Existe: ${newItem.id}") //id que vem do produto

                        listReference.document(iDList).collection(idNameList)
                            .get().addOnSuccessListener { docs ->
                                for (doc in docs) {
                                    if (doc.get("id").toString().equals(newItem.id)) {
                                        exist = true
//                                        Log.d(
//                                            "Ittens",
//                                            "doc.get(id): ${
//                                                doc.get("id").toString()
//                                            }, doc.id: ${doc.id},idProd: ${doc.get("id")}, dados completos: ${doc.data} "
//                                        )
                                    }
                                }
                                if (!exist) {
                                    listReference.document(iDList).collection(idNameList)
                                        .add(newItem)
                                }

                            }
                    }
                }
        }
    }

    //recebe um produto para atualizar e apaga se estoque for zero
    suspend fun updateItemLista(produto: Produto) {
        Log.d("updatez", "cheguei idy: ${produto.id} nome da lista: $nameListFull")
        val user = FirebaseAuth.getInstance().currentUser //pega o id do usuario
        user?.uid?.let { uid ->
            val listReference = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("listas_de_compras")
            listReference.whereEqualTo("nome_da_lista", nameListFull).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val idList = document.id
                        val idNameList = document.get("nome_da_lista").toString()
                        //Log.d("updatez", "idListz: $idList,  idNameList: $idNameList")
                        listReference.document(idList).collection(idNameList).get()
                            .addOnSuccessListener { docs ->
                                for (doc in docs) {
                                    if (doc.get("id") == produto.id) {
                                        if(produto.quantidade>0) {
                                            val dados = hashMapOf(
                                                "quantidade" to produto.quantidade,
                                                "valor_s1" to produto.valor_s1,
                                                "valor_s2" to produto.valor_s2,
                                                "valor_s3" to produto.valor_s3
                                            )
                                            //atualiza a lista criada com preços de produtos
                                            listReference.document(idList).collection(idNameList)
                                                .document(doc.id)
                                                .update(dados as Map<String, Any>) //.update("quantidade", produto.quantidade )
                                        }else{
                                            listReference.document(idList).collection(idNameList)
                                                .document(doc.id).delete()
                                        }

                                        Log.d(
                                            "updatez",
                                            "idListz: ${doc.id},  qtde: ${produto.quantidade}   doc.get(id): ${
                                                doc.get("id")
                                            }"
                                        )
                                    }
                                }
                            }
                    }
                }


        }

    }
}
