package ru.practicum.android.diploma.ui.root

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.practicum.android.diploma.di.dataModule
import ru.practicum.android.diploma.di.interactorModule
import ru.practicum.android.diploma.di.repositoryModule
import ru.practicum.android.diploma.di.uiModule
import ru.practicum.android.diploma.di.viewModelModule
import ru.practicum.android.diploma.domain.impl.UpdateDbOnAppStartUseCase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Функция, которая настраивает библиотеку Koin, нужно вызвать перед использованием
        startKoin {
            // Метод специального класса, переданного как this, для добавления контекста в граф
            androidContext(this@App)
            // Передаём все модули, чтобы их содержимое было передано в граф
            modules(repositoryModule, interactorModule, viewModelModule, dataModule, uiModule)
        }

        val dataUpdater: UpdateDbOnAppStartUseCase by inject()

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            runCatching {
                dataUpdater()
            }.onSuccess {
                Log.d("WWW", "all ok")
            }.onFailure {
                Log.d("WWW", "DB fail")
            }
        }
    }
}
