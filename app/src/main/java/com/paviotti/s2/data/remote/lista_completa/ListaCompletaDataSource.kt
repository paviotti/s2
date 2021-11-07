package com.paviotti.s2.data.remote.lista_completa

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paviotti.s2.data.model.Produto
import kotlinx.coroutines.tasks.await
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.SomaLista
import com.paviotti.s2.data.model.VarStatic.Companion.id_s1
import com.paviotti.s2.data.model.VarStatic.Companion.id_s2
import com.paviotti.s2.data.model.VarStatic.Companion.id_s3
import com.paviotti.s2.data.model.VarStatic.Companion.nameListFull
import com.paviotti.s2.data.model.VarStatic.Companion.nome_s1
import com.paviotti.s2.data.model.VarStatic.Companion.nome_s2
import com.paviotti.s2.data.model.VarStatic.Companion.nome_s3
import com.paviotti.s2.data.model.VarStatic.Companion.total_s1
import com.paviotti.s2.data.model.VarStatic.Companion.total_s2
import com.paviotti.s2.data.model.VarStatic.Companion.total_s3

class ListaCompletaDataSource {
    var tot_l1 = 0.0
    var tot_l2 = 0.0
    var tot_l3 = 0.0

    /** Esta classe lê a tabela produto completa (principal)*/
    suspend fun getLatestListaCompleta(): Result<List<Produto>> {
        tot_l1 = 0.0
        tot_l2 = 0.0
        tot_l3 = 0.0
        findSupermarketSelected() //chama quando entra em qualquer lista
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
                    Log.d("produto", "itemList.id: ${itemList.id} ${itemList.get("descricao")}")
                    /** passa os 3 preços para a lista*/
                    itens.valor_s1 = findPrice1(itemList.id)
                    itens.valor_s2 = findPrice2(itemList.id)
                    itens.valor_s3 = findPrice3(itemList.id)

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
        return Result.Success(listProdutos)
    }

    //=====================
    suspend fun findPrice1(idProduct: String): Double {
        val reference =
            FirebaseFirestore.getInstance().collection("produtos").document(idProduct)
                .collection("supermercados").document(id_s1).get().await()
        val preco1 = (reference.get("preco")).toString()
        Log.d("precoProd", "IdSup:${id_s1}  preço:$preco1 ")
        return preco1.toDouble()
    }

    suspend fun findPrice2(idProduct: String): Double {
        val reference =
            FirebaseFirestore.getInstance().collection("produtos").document(idProduct)
                .collection("supermercados").document(id_s2).get().await()
        val preco1 = (reference.get("preco")).toString()
        Log.d("precoProd", "IdSup:${id_s2}  preço:$preco1 ")
        return preco1.toDouble()
    }
    suspend fun findPrice3(idProduct: String): Double {
        val reference =
            FirebaseFirestore.getInstance().collection("produtos").document(idProduct)
                .collection("supermercados").document(id_s3).get().await()
        val preco1 = (reference.get("preco")).toString()
        Log.d("precoProd", "IdSup:${id_s3}  preço:$preco1 ")
        return preco1.toDouble()
    }


    //=========================================================
    //pega os ids dos supermercados selecionados e armazena em variavel public
    suspend fun findSupermarketSelected() {
        var qtde = 0
        id_s1 = ""
        id_s2 = ""
        id_s3 = ""
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            val referenceSupermarket = FirebaseFirestore.getInstance().collection("supermercados")
            val idsInSupermercado = referenceSupermarket.get().await()
            for (idSup in idsInSupermercado) {

                val referenceIdUserSupermercado =
                    FirebaseFirestore.getInstance().collection("supermercados").document(idSup.id)
                        .collection("users").get().await()
                referenceIdUserSupermercado.documents.forEach { itens ->
                    qtde++
                    Log.d("supermercados", "itens.idSup: ${idSup.id}")
                    Log.d("supermercados", "itens.Nome: ${idSup.get("nome_fantasia")}")
                    //   Log.d("supermercados", "itens.ids: ${itens.get("uid")}")

                    when (qtde) {
                        1 -> {
                            id_s1 = idSup.id
                            nome_s1 = idSup.get("nome_fantasia").toString()
                        }
                        2 -> {
                            id_s2 = idSup.id
                            nome_s2 = idSup.get("nome_fantasia").toString()
                        }
                        3 -> {
                            id_s3 = idSup.id
                            nome_s3 = idSup.get("nome_fantasia").toString()
                        }
                    }
                    Log.d(
                        "supermercadosx",
                        "soma: ${qtde} ids1: $id_s1, ids1: $id_s2, ids1: $id_s3"
                    )
                }
            }
        }
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
                                    }
                                }
                            }

                    }

                }


        }

    }

    suspend fun updateSun(produto: Produto) {
        var total_sl1 = 0.0
        var total_sl2 = 0.0
        var total_sl3 = 0.0
        val user = FirebaseAuth.getInstance().currentUser //pega o id do usuario
        user?.uid?.let { uid ->
            val listReference = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("listas_de_compras").whereEqualTo("nome_da_lista", nameListFull).get()
                .await()
            listReference
            for (document in listReference.documents) {
                val idList = document.id
                val idNameList = document.get("nome_da_lista").toString()
                val listReference2 =
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                        .collection("listas_de_compras").document(idList).collection(idNameList)
                        .get().await()
                for (doc in listReference2.documents) {
                    if (doc.get("id") == produto.id) {
                        /**O processo de soma está correto - soma cada linha */
                        total_sl1 += (doc.get("quantidade") as Double * doc.get("valor_s1") as Double)
                        total_sl2 += (doc.get("quantidade") as Double * doc.get("valor_s2") as Double)
                        total_sl3 += (doc.get("quantidade") as Double * doc.get("valor_s3") as Double)

                        //acumula as linhas
                        tot_l1 += total_sl1
                        tot_l2 += total_sl2
                        tot_l3 += total_sl3
                    }
                }

            }

        }
    }
}
