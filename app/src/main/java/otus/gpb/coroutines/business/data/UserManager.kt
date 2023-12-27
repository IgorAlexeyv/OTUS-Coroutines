package otus.gpb.coroutines.business.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Предоставляет текущего пользователя
 */
interface UserProvider {
    /**
     * Поток данных о пользователе
     */
    val currentUserFlow: Flow<User>

    /**
     * Получить текущего пользователя
     */
    fun getCurrentUser(): User
}

/**
 * Управляет текущим пользователем
 */
interface UserManager : UserProvider {
    /**
     * Установить нового пользователя
     */
    suspend fun setCurrentUser(user: User)
}

class UserManagerImpl : UserManager {
    /**
     * Состояние данных о пользователе
     */
    private val userState: MutableStateFlow<User> = MutableStateFlow(User.NoUser)

    /**
     * Установить нового пользователя
     */
    override suspend fun setCurrentUser(user: User) {
        userState.emit(user)
    }

    /**
     * Поток данных о пользователе
     */
    override val currentUserFlow: Flow<User> get() = userState.asStateFlow()

    /**
     * Получить текущего пользователя
     */
    override fun getCurrentUser(): User = userState.value

}