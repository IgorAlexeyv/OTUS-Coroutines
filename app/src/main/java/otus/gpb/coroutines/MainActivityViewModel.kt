package otus.gpb.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import otus.gpb.coroutines.network.Api
import otus.gpb.coroutines.network.data.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        Log.i(TAG, "Logging in $name...")
        mUiState.value = MainActivityViewState.Loading
        call = service.login(name, password).apply {
            enqueue(object : Callback<LoginResponse>{
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Log.i(TAG, "Successfully logged-in user with id: ${response.body()?.id}")
                    mUiState.value = MainActivityViewState.Content
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.w(TAG, "Login error", t)
                    mUiState.value = MainActivityViewState.Login
                }
            })
        }
    }

    /**
     * Выгрузить пользователя
     */
    fun logout() {
        mUiState.value = MainActivityViewState.Login
    }

    /**
     * Вызывается, когда модель больше не нужна
     */
    override fun onCleared() {
        call?.cancel()
    }

    companion object {
        const val TAG = "MainActivityViewModel"
    }
}