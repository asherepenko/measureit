package com.sherepenko.android.measureit

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.net.SocketException
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

        setupTimber()
        setupRx()
        setupKoin()
    }

    private fun setupTimber() {
        Timber.plant(CrashlyticsTree())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupRx() {
        RxJavaPlugins.setErrorHandler { e ->
            var error = e

            if (error is UndeliverableException && error.cause != null) {
                error = error.cause!!
            }

            if (error is IOException || error is SocketException) {
                return@setErrorHandler
            }

            if (error is InterruptedException) {
                return@setErrorHandler
            }

            if (error is NullPointerException ||
                error is IllegalArgumentException ||
                error is IllegalStateException
            ) {
                Thread.currentThread().uncaughtExceptionHandler
                    ?.uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }

            Timber.w(error)
        }
    }

    private fun setupKoin() {
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

internal class CrashlyticsTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean =
        priority == Log.WARN || priority == Log.ERROR || priority == Log.ASSERT

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        FirebaseCrashlytics.getInstance().apply {
            log(message)

            throwable?.let {
                recordException(it)
                sendUnsentReports()
            }
        }
    }
}
