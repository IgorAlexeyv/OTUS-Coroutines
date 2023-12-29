package otus.gpb.coroutines.business.data

/**
 * Состояние загрузки данных
 */
sealed class LceState<out D: Any> {
    /**
     * Данные
     */
    abstract val data: D?

    /**
     * Загрузка данных
     * @property data Данные, если есть
     */
    data class Loading<out DATA: Any>(override val data: DATA?): LceState<DATA>()

    /**
     * Данные загружены
     */
    data class Content<out DATA: Any>(override val data: DATA): LceState<DATA>()

    /**
     * Ошибка
     * @property data Данные, если есть
     * @property error Data load error
     */
    data class Error<out DATA: Any>(override val data: DATA?, val error: Throwable): LceState<DATA>()
}
