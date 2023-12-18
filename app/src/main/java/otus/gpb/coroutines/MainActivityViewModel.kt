package otus.gpb.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import otus.gpb.coroutines.network.Api
import otus.gpb.coroutines.network.networkCall

/**
 * Модель основной активити
 * - отслеживает активность пользователя
 * - обрабатывает логин пользователя
 */
class MainActivityViewModel : ViewModel() {
    /**
     * Сетевой сервис
     */
    private val service = Api.create()

    /**
     * Аналог лайвдаты (пока что)
     */
    private val mUiState = MutableLiveData<MainActivityViewState>(MainActivityViewState.Login)

    /**
     * Состояние экрана
     */
    val uiState: LiveData<MainActivityViewState> get() = mUiState

    /**
     * Логин пользователя
     */
    fun login(name: String, password: String) {
        viewModelScope.launch {
            Log.i(TAG, "Logging in $name...")
            mUiState.value = MainActivityViewState.Loading
            try {
                val profile = withContext(Dispatchers.IO) {
                    Log.i(TAG, "When loading done, I'm on a thread: ${Thread.currentThread().name}")

                    val loginResponse = networkCall { service.login(name, password) }
                    Log.i(TAG, "Successfully logged-in user with id: ${loginResponse.id}")

                    val profileResponse = networkCall { service.getProfile(loginResponse.token, loginResponse.id) }
                    Log.i(TAG, "Successfully loaded profile for: ${profileResponse.name}")

                    return@withContext profileResponse
                }

                Log.i(TAG, "When loading done, I'm on a thread: ${Thread.currentThread().name}")
                mUiState.value = MainActivityViewState.Content(profile.name)
            } catch (t: Throwable) {
                Log.w(TAG, "Login error", t)
                mUiState.value = MainActivityViewState.Login
            }
        }
    }

    /**
     * Выгрузить пользователя
     */
    fun logout() {
        mUiState.value = MainActivityViewState.Login
    }

    companion object {
        const val TAG = "MainActivityViewModel"
    }
}