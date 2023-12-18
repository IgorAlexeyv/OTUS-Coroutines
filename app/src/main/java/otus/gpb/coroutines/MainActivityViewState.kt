package otus.gpb.coroutines

/**
 * Состояние основного экрана
 */
sealed class MainActivityViewState {
    /**
     * Нет активного пользователя - показываем логин/пароль
     */
    data object Login: MainActivityViewState()

    /**
     * Загрузка
     */
    data object Loading: MainActivityViewState()

    /**
     * Есть активный пользователь - показываем содержимое
     */
    data object Content: MainActivityViewState()
}