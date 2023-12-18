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
import retrofit2.Call

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
     * Текущий запрос к серверу
     */
    private var call: Call<*>? = null;

    /**
     * Логин пользователя
     */
    fun login(name: String, password: String) {
        viewModelScope.launch {
            Log.i(TAG, "Logging in $name...")
            try {
                val profile = withContext(Dispatchers.IO) {
                    Log.i(TAG, "When loading done, I'm on a thread: ${Thread.currentThread().name}")

                    val loginCall = service.login(name, password).execute()
                    val loginResponse = loginCall.body()
                    if (null == loginResponse) {
                        Log.w(TAG, "Empty login response")
                        throw NoSuchElementException("No login response")
                    }
                    Log.i(TAG, "Successfully logged-in user with id: ${loginResponse.id}")

                    val profileCall = service.getProfile(loginResponse.token, loginResponse.id).execute()
                    val profileResponse = profileCall.body()
                    if (null == profileResponse) {
                        Log.w(TAG, "Empty profile response")
                        throw NoSuchElementException("No login response")
                    }
                    return@withContext profileResponse
                }

                Log.i(TAG, "When loading done, I'm on a thread: ${Thread.currentThread().name}")
                Log.i(TAG, "Successfully loaded profile for: ${profile.name}")
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