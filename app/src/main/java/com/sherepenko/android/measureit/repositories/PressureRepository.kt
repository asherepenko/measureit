package com.sherepenko.android.measureit.repositories

import com.sherepenko.android.measureit.data.PressureItem
import com.sherepenko.android.measureit.providers.PressureDataSource
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit

interface PressureRepository {

    fun getPressure(): Flowable<PressureItem>
}

class PressureRepositoryImpl(
    private val dataSource: PressureDataSource
) : PressureRepository {

    companion object {
        private const val MIN_EMIT_TIMEOUT_MS = 500L
    }

    override fun getPressure(): Flowable<PressureItem> =
        dataSource.getPressure()
            .onBackpressureLatest()
            .debounce(MIN_EMIT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
}
