package otus.gpb.coroutines.network.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Post(val id: Long, val title: String, val created: Instant)