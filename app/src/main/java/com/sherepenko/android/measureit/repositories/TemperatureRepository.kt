package com.sherepenko.android.measureit.repositories

import com.sherepenko.android.measureit.data.TemperatureItem
import com.sherepenko.android.measureit.providers.TemperatureDataSource
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit

interface TemperatureRepository {

    fun getTemperature(): Flowable<TemperatureItem>
}

class TemperatureRepositoryImpl(
    private val dataSource: TemperatureDataSource
) : TemperatureRepository {

    companion object {
        private const val MIN_EMIT_TIMEOUT_MS = 500L
    }

    override fun getTemperature(): Flowable<TemperatureItem> =
        dataSource.getTemperature()
            .onBackpressureLatest()
            .debounce(MIN_EMIT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
}
