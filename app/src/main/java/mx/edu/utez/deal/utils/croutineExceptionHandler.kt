package mx.edu.utez.deal.utils

import kotlinx.coroutines.CoroutineExceptionHandler

object coroutineExceptionHandler{
    val handler = CoroutineExceptionHandler { _, exception->
        println("coroutine got $exception")
    }
}