package com.sherepenko.android.measureit.providers

import com.sherepenko.android.measureit.data.PressureItem
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import timber.log.Timber

interface PressureDataSource {

    fun getPressure(): Flowable<PressureItem>
}

class PressureDataSourceImpl : PressureDataSource {

    companion object {
        private const val TAG = "Pressure"

        private const val MIN_DELAY_MS = 10L
        private const val MAX_DELAY_MS = 2000L
        private const val MAX_DELAY_THRESHOLD_MS = 1500L

        private fun nextDelay(): Long =
            Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS)

        private fun nextItem(): PressureItem =
            PressureItem(Random.nextDouble(PressureItem.MIN_VALUE, PressureItem.MAX_VALUE))
    }

    override fun getPressure(): Flowable<PressureItem> =
        Flowable
            .defer {
                Flowable.just(nextItem())
                    .delay(nextDelay(), TimeUnit.MILLISECONDS)
                    .timeout(MAX_DELAY_THRESHOLD_MS, TimeUnit.MILLISECONDS)
            }
            .onErrorResumeWith(Flowable.just(PressureItem.empty()))
            .timeInterval(TimeUnit.MILLISECONDS)
            .doOnNext {
                Timber
                    .tag(TAG)
                    .d("New item ${it.value()} emitted after ${it.time()} ms")
            }
            .map {
                it.value()
            }
            .onBackpressureDrop()
            .repeat()
}
