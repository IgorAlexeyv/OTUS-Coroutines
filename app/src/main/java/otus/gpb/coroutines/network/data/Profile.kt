package otus.gpb.coroutines.network.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Long,
    val name: String,
    val age: Int,
    val registered: Instant,
    val interests: List<String>
)