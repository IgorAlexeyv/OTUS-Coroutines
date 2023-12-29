package otus.gpb.coroutines

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import otus.gpb.coroutines.business.data.LceState
import otus.gpb.coroutines.business.data.User
import otus.gpb.coroutines.business.data.UserProvider
import otus.gpb.coroutines.network.Api
import otus.gpb.coroutines.network.PostsUsecase
import otus.gpb.coroutines.network.data.Post

/**
 * Модель загрузки записей в блоге
 * - отслеживает текущего пользователя
 * - загружает список его постов
 */
class PostsFragmentViewModel(userProvider: UserProvider) : ViewModel() {
    /**
     * Сетевой сервис
     */
    private val service = Api.create()

    /**
     * Use-case
     */
    private val posts = PostsUsecase.Impl(service)

    /**
     * Состояние экрана
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<LceState<List<Post>>> = userProvider.currentUserFlow.flatMapLatest {
        when(it) {
            User.NoUser -> flowOf(LceState.Loading(null))
            is User.ActiveUser -> posts.load(it.token).flowOn(Dispatchers.IO)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LceState.Loading(null))

    /**
     * Фабрика
     */
    class Factory(application: Application) : ViewModelProvider.Factory {
        private val cApplication: CoroutinesApplication = application as CoroutinesApplication

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = PostsFragmentViewModel(cApplication.userManager) as T
    }
}
