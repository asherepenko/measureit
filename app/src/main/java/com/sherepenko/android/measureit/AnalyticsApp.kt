package com.sherepenko.android.measureit

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.sherepenko.android.measureit.providers.HumidityDataSourceImpl
import com.sherepenko.android.measureit.providers.PressureDataSourceImpl
import com.sherepenko.android.measureit.providers.TemperatureDataSourceImpl
import com.sherepenko.android.measureit.repositories.HumidityRepository
import com.sherepenko.android.measureit.repositories.HumidityRepositoryImpl
import com.sherepenko.android.measureit.repositories.PressureRepository
import com.sherepenko.android.measureit.repositories.PressureRepositoryImpl
import com.sherepenko.android.measureit.repositories.TemperatureRepository
import com.sherepenko.android.measureit.repositories.TemperatureRepositoryImpl
import com.sherepenko.android.measureit.viewmodels.DashboardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class AnalyticsApp : Application() {

    private val appModule = module {
    }

    private val repositoryModule = module {
        single<HumidityRepository> {
            HumidityRepositoryImpl(
                HumidityDataSourceImpl()
            )
        }

        single<PressureRepository> {
            PressureRepositoryImpl(
                PressureDataSourceImpl()
            )
        }

        single<TemperatureRepository> {
            TemperatureRepositoryImpl(
                TemperatureDataSourceImpl()
            )
        }
    }

    private val viewModelModule = module {
        viewModel {
            DashboardViewModel(get(), get(), get())
        }
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this@AnalyticsApp)

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@AnalyticsApp)
            modules(
                appModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}
