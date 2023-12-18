package otus.gpb.coroutines.business.data

import otus.gpb.coroutines.network.data.Profile

/**
 * Текущий пользователь системы
 */
sealed class User {
    /**
     * Нет активного пользователя
     */
    data object NoUser : User()

    /**
     * Активный пользователь
     */
    data class ActiveUser(val token: String, val profile: Profile) : User()
}