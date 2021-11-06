package com.paviotti.s2.data.remote.ListaDeListas

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.ListaDeListas
import com.paviotti.s2.data.model.Produto
import kotlinx.coroutines.tasks.await
import java.lang.invoke.MethodHandles

//https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/24810836#overview
/** este método vai buscar informações no Firebase*/
class ListaDeListasDataSource {
    suspend fun getLatestListas(): Result<List<ListaDeListas>> {
        val listListas = mutableListOf<ListaDeListas>()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            /** get() pega tudo de dentro da pasta listas_de_compras */
            val querySnapshot =
                FirebaseFirestore.getInstance().collection("users").document(uid)
                    .collection("listas_de_compras").get().await()
//                FirebaseFirestore.getInstance().collection("users").document()
//                    .get().await()

            // pega cada documento dentro de listas_de_compras
            for (itemList in querySnapshot.documents) {
                itemList.toObject(ListaDeListas::class.java)?.let { itens ->
                    listListas.add(itens)
                }
            }
        }
      //  criaListaProdutos() /* cuidado, gera 10 registros em branco **/
//        Grava dados, usei para teste
//        var bd = FirebaseFirestore.getInstance()
//        var reference = bd.collection("listas_de_compras")
//        var data = hashMapOf("userId" to "123", "nome_da_lista" to "casa" )
//        reference.document().set(data)
        return Result.Success(listListas)
    }

    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/26519106#announcements
    /**
     * listaDeListasFragment pega a variavel da tela e passa para -> ListasDeListasViewModel
     * ListasDeListasViewModel -> (interface) ListasDeListasRepository
     * (interface) ListasDeListasRepository -> implementa  ListasDeListasRepositoryImplement
     *  ListasDeListasRepositoryImplement ->  ListasDeListasImplement
     * recebe os dados de Repository.Implement e manda para o Firebase
     * */
    suspend fun createNewItem(newItem: String) {
        //  Log.d("dataSource","dataSource: $newItem")
        //pega o usuário corrente para gravar junto com o nome de suas listas
        val user = FirebaseAuth.getInstance().currentUser //pega o usuario autenticado
        user?.uid?.let { uid ->
            val userReference = FirebaseFirestore.getInstance().collection("users")
            userReference.document(uid) //id do usuário
                .collection("listas_de_compras").document().set(ListaDeListas(newItem)).await()
           // Log.d("Var", "newItem: $newItem")
        }
    }

    //cria um produto em branco - cuidado
    fun criaListaProdutos() {
        for(i in 1..10) {
            val userReference = FirebaseFirestore.getInstance().collection("produtos")
            userReference.add(Produto("", "Mercearia"))
        }
    }
}