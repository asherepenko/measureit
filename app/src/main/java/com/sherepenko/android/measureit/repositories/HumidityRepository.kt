package com.sherepenko.android.measureit.repositories

import com.sherepenko.android.measureit.data.HumidityItem
import com.sherepenko.android.measureit.providers.HumidityDataSource
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit

interface HumidityRepository {

    fun getHumidity(): Flowable<HumidityItem>
}

class HumidityRepositoryImpl(
    private val dataSource: HumidityDataSource
) : HumidityRepository {

    companion object {
        private const val MIN_EMIT_TIMEOUT_MS = 500L
    }

    override fun getHumidity(): Flowable<HumidityItem> =
        dataSource.getHumidity()
            .onBackpressureLatest()
            .debounce(MIN_EMIT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
}
