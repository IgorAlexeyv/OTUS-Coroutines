package otus.gpb.coroutines

import android.app.Application
import otus.gpb.coroutines.business.data.UserManagerImpl

class CoroutinesApplication : Application() {
    /**
     * Общее для всего приложения
     */
    val userManager = UserManagerImpl()
}