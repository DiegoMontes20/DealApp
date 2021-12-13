package mx.edu.utez.deal.util

import kotlinx.coroutines.CoroutineExceptionHandler


object coroutineExceptionHandler{
    val handler = CoroutineExceptionHandler {context, exception->
        println("coroutine got $exception")
    }
}