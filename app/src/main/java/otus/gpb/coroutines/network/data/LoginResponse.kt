package otus.gpb.coroutines.network.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val id: Long, val token: String)