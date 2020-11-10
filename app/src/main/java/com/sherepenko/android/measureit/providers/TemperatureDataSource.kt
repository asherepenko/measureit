package com.sherepenko.android.measureit.providers

import com.sherepenko.android.measureit.data.TemperatureItem
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import timber.log.Timber

interface TemperatureDataSource {

    fun getTemperature(): Flowable<TemperatureItem>
}

class TemperatureDataSourceImpl : TemperatureDataSource {

    companion object {
        private const val TAG = "Pressure"

        private const val MIN_DELAY_MS = 15L
        private const val MAX_DELAY_MS = 1750L
        private const val MAX_DELAY_THRESHOLD_MS = 1250L

        private fun nextDelay(): Long =
            Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS)

        private fun nextItem(): TemperatureItem =
            TemperatureItem(Random.nextDouble(TemperatureItem.MIN_VALUE, TemperatureItem.MAX_VALUE))
    }

    override fun getTemperature(): Flowable<TemperatureItem> =
        Flowable.defer {
            Flowable.just(nextItem())
                .delay(nextDelay(), TimeUnit.MILLISECONDS)
                .timeout(MAX_DELAY_THRESHOLD_MS, TimeUnit.MILLISECONDS)
        }
            .onErrorResumeWith(Flowable.just(TemperatureItem.empty()))
            .timeInterval(TimeUnit.MILLISECONDS)
            .doOnNext {
                Timber
                    .tag(TAG)
                    .d("New item ${it.value()} emitted after ${it.time()} ms")
            }
            .map { it.value() }
            .onBackpressureDrop()
            .repeat()
}
