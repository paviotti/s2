package com.paviotti.s2.data.remote.lista_completa

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paviotti.s2.data.model.Produto
import kotlinx.coroutines.tasks.await
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.SomaLista
import com.paviotti.s2.data.model.VarStatic.Companion.nameListFull
import com.paviotti.s2.data.model.VarStatic.Companion.total_s1
import com.paviotti.s2.data.model.VarStatic.Companion.total_s2
import com.paviotti.s2.data.model.VarStatic.Companion.total_s3

class ListaCompletaDataSource {


    /** Esta classe lê a tabela produto completa (principal)*/
    suspend fun getLatestListaCompleta(): Result<List<Produto>> {
        val listProdutos = mutableListOf<Produto>()
        val listProd = mutableListOf<Produto>()
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
                                            //soma os itens da compra por supermercado - passou para função updateSum
//                                            total_s1 = doc.get("quantidade") as Double *doc.get("valor_s1") as Double
//                                            total_s2 = doc.get("quantidade") as Double *doc.get("valor_s2") as Double
//                                            total_s3 = doc.get("quantidade") as Double *doc.get("valor_s3") as Double
//
                                            Log.d(
                                                "getList",
                                                "Total1: $total_s1 , total2: $total_s2, total3: $total_s3"
                                            )

                                            // Log.d("itens", "Qtde ${itens.quantidade}")
//                                            Log.d("Itens", "doc.get: ${doc.get("photo_url")}")
//                                            Log.d("Itens", "itens.photo_url: ${itens.photo_url}")
                                            // criar outra lista
                                            // val produtoFinal = doc.toObject(Produto::class.java)
                                            // listProd.add(produtoFinal)
                                        }

                                    }

                                }
                        }
                    }
                    listProdutos.add(itens) //cria uma lista de Produto
                    updateSun(itens)
                }
            }
        }
        total_s1 = tot_l1
        total_s2 = tot_l2
        total_s3 = tot_l3
        Log.d("fragmentx2", " TT1: $tot_l1, TT1: $tot_l2, TT1: $tot_l3")
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

    /**recebe um produto para atualizar e apaga se estoque for zero*/
    suspend fun updateItemLista(produto: Produto) {
        // Log.d("updatez", "cheguei idy: ${produto.id} nome da lista: $nameListFull")
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
                                        if (produto.quantidade > 0) {
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
                                        } else {
                                            listReference.document(idList).collection(idNameList)
                                                .document(doc.id).delete()
                                        }

//                                        Log.d(
//                                            "updatez",
//                                            "idListz: ${doc.id},  qtde: ${produto.quantidade}   doc.get(id): ${
//                                                doc.get("id")
//                                            }"
//                                        )
                                    }
                                }
                            }

                    }

                }


        }

    }

    //--------------- updateSun ------------
    var tot_l1 = 0.0
    var tot_l2 = 0.0
    var tot_l3 = 0.0
    suspend fun updateSun(produto: Produto) {
        var total_sl1 = 0.0
        var total_sl2 = 0.0
        var total_sl3 = 0.0

        //   Log.d("atual", "TotaisAcima:$tot1")
        val listProdutosAtualizado =
            mutableListOf<Produto>() //cria uma nova lista com produtos atualizados
        val user = FirebaseAuth.getInstance().currentUser //pega o id do usuario
        user?.uid?.let { uid ->
            val listReference = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("listas_de_compras").whereEqualTo("nome_da_lista", nameListFull).get()
                .await()
            listReference
            //   .addOnSuccessListener { documents ->

            for (document in listReference.documents) {
                val idList = document.id
                val idNameList = document.get("nome_da_lista").toString()
                //Log.d("updatez", "idListz: $idList,  idNameList: $idNameList")

                val listReference2 =
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                        .collection("listas_de_compras").document(idList).collection(idNameList)
                        .get().await()
                // listReference2
                //    .addOnSuccessListener { docs ->
                for (doc in listReference2.documents) {
                    if (doc.get("id") == produto.id) {
                        /**O processo de soma está correto - soma cada linha */
                        total_sl1 += (doc.get("quantidade") as Double * doc.get("valor_s1") as Double)
                        total_sl2 += (doc.get("quantidade") as Double * doc.get("valor_s2") as Double)
                        total_sl3 += (doc.get("quantidade") as Double * doc.get("valor_s3") as Double)
                        tot_l1 += total_sl1
                        tot_l2 += total_sl2
                        tot_l3 += total_sl3

                        //listProdutosAtualizado.add(produto)
                        Log.d("updateDadosdaLinha", "listatual: ${doc.data}")
                    }
                }

//                                for (prod in listProdutosAtualizado) {
//                                    var quantidade = prod.quantidade
//                                }

                // }
            }

            // }
        }
        // Log.d("updateSomaLinha", " TT1: $tot_l1, TT1: $tot_l2, TT1: $tot_l3")
    }


}
