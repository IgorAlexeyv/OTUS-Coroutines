package otus.gpb.coroutines.network

import retrofit2.Call
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend inline fun <T : Any> networkCall(crossinline block: () -> Call<T>): T = suspendCoroutine {
    try {
        val response = block().execute()
        val body = response.body()
        if (null == body) {
            it.resumeWithException(NoSuchElementException("Empty data"))
        } else {
            it.resume(body)
        }
    } catch (t : Throwable) {
        it.resumeWithException(t)
    }
}