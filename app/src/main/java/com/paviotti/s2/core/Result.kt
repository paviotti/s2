package com.paviotti.s2.core

import java.lang.Exception

//https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/24810830#questions/13960482
//retorna uma lista do que quiser, na sequencia
sealed class Result<out T> {
    class Loading<out T> : Result<T>() //retorna um construtor vazio
    data class Success<out T>(val data: T) : Result<T>() //retorna uma lista ou qualquer outro dado
    data class Failure(val exception: Exception) : Result<Nothing>()//retorna qualquer tipo de exceção
}