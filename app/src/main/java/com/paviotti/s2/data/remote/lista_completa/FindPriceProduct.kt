package com.paviotti.s2.data.remote.lista_completa

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//procura o preço do produto nos supermercados que tivere preço em produto/documenteo/supermercados
class FindPriceProduct {

    suspend fun findPrice(idProduct: String) {
        val reference =
            FirebaseFirestore.getInstance().collection("produtos").document(idProduct)
                .collection("supermercados").get().await()
        reference.documents.forEach { priceProd ->
        //    Log.d("precoProd","preço: ${priceProd.data}")

        }

    }


}