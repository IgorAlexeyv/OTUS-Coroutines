package otus.gpb.coroutines.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import otus.gpb.coroutines.business.data.LceState
import otus.gpb.coroutines.network.data.Post

/**
 * Загрузка постов
 */
interface PostsUsecase {
    /**
     * Загружает данные
     */
    fun load(token: String): Flow<LceState<List<Post>>>

    class Impl(private val api: Api) : PostsUsecase {
        /**
         * Загружает данные
         */
        override fun load(token: String): Flow<LceState<List<Post>>> = flow {
            emit(LceState.Loading(null))
            try {
                emit(LceState.Content(networkCall { api.getPosts(token) }))
            } catch (e: Throwable) {
                emit(LceState.Error(null, e))
            }
        }
    }
}