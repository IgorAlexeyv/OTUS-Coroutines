package otus.gpb.coroutines.network.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)