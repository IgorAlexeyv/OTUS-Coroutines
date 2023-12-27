package otus.gpb.coroutines

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import otus.gpb.coroutines.business.data.User
import otus.gpb.coroutines.business.data.UserManager
import otus.gpb.coroutines.network.Api
import otus.gpb.coroutines.network.networkCall

/**
 * Модель основной активити
 * - отслеживает активность пользователя
 * - обрабатывает логин пользователя
 */
class MainActivityViewModel(private val userManager: UserManager) : ViewModel() {
    /**
     * Сетевой сервис
     */
    private val service = Api.create()

    /**
     * Операция загрузки
     */
    private val loading = MutableStateFlow(false)

    /**
     * Состояние экрана
     */
    val uiState: StateFlow<MainActivityViewState> = combine(loading, userManager.currentUserFlow) { l, u ->
        when {
            l -> MainActivityViewState.Loading
            u is User.ActiveUser -> MainActivityViewState.Content(u.profile.name)
            else -> MainActivityViewState.Login
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainActivityViewState.Loading)

    /**
     * Логин пользователя
     */
    fun login(name: String, password: String) {
        viewModelScope.launch {
            Log.i(TAG, "Logging in $name...")
            loading.value = true
            try {
                withContext(Dispatchers.IO) {
                    Log.i(TAG, "When loading done, I'm on a thread: ${Thread.currentThread().name}")

                    val loginResponse = networkCall { service.login(name, password) }
                    Log.i(TAG, "Successfully logged-in user with id: ${loginResponse.id}")

                    val profileResponse = networkCall { service.getProfile(loginResponse.token, loginResponse.id) }
                    Log.i(TAG, "Successfully loaded profile for: ${profileResponse.name}")

                    userManager.setCurrentUser(User.ActiveUser(loginResponse.token, profileResponse))
                }
            } catch (t: Throwable) {
                Log.w(TAG, "Login error", t)
            } finally {
                loading.value = false
            }
        }
    }

    /**
     * Выгрузить пользователя
     */
    fun logout() = viewModelScope.launch {
        userManager.setCurrentUser(User.NoUser)
    }

    companion object {
        const val TAG = "MainActivityViewModel"
    }

    class Factory(application: Application) : ViewModelProvider.Factory {
        private val cApplication: CoroutinesApplication = application as CoroutinesApplication

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainActivityViewModel(cApplication.userManager) as T
    }
}