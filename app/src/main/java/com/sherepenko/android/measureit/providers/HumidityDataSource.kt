package com.sherepenko.android.measureit.providers

import com.sherepenko.android.measureit.data.HumidityItem
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import timber.log.Timber

interface HumidityDataSource {

    fun getHumidity(): Flowable<HumidityItem>
}

class HumidityDataSourceImpl : HumidityDataSource {

    companion object {
        private const val TAG = "Humidity"

        private const val MIN_DELAY_MS = 50L
        private const val MAX_DELAY_MS = 1500L
        private const val MAX_DELAY_THRESHOLD_MS = 1000L

        private fun nextDelay(): Long =
            Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS)

        private fun nextItem(): HumidityItem =
            HumidityItem(Random.nextDouble(HumidityItem.MIN_VALUE, HumidityItem.MAX_VALUE))
    }

    override fun getHumidity(): Flowable<HumidityItem> =
        Flowable.defer {
            Flowable.just(nextItem())
                .delay(nextDelay(), TimeUnit.MILLISECONDS)
                .timeout(MAX_DELAY_THRESHOLD_MS, TimeUnit.MILLISECONDS)
        }
            .onErrorResumeWith(Flowable.just(HumidityItem.empty()))
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
