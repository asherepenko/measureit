package com.sherepenko.android.measureit.data

data class HumidityItem(
    val value: Double
) {
    companion object {
        const val MIN_VALUE = 10.0 // %
        const val MAX_VALUE = 100.0 // %

        fun empty(): HumidityItem =
            HumidityItem(Double.NaN)
    }

    override fun toString(): String =
        "humidity: { value: ${"%.2f".format(value)}, unit: \"%\" }"
}

fun HumidityItem.isEmpty(): Boolean =
    value.isNaN()

fun HumidityItem?.isNullOrEmpty(): Boolean =
    this == null || isEmpty()
