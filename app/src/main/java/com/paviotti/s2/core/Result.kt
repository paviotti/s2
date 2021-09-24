package com.paviotti.s2.core

import java.lang.Exception

//https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/24810830#questions/13960482
//retorna uma lista do que quizer, na sequencia
sealed class Result<out T> {
    class Loading<out T> : Result<T>() //retorna um construtor vazio
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()//retorna qualquer tipo de exc eção
}