package com.sherepenko.android.measureit.data

data class TemperatureItem(
    val value: Double
) {
    companion object {
        const val MIN_VALUE = 25.0 // C
        const val MAX_VALUE = 250.0 // C

        fun empty(): TemperatureItem =
            TemperatureItem(Double.NaN)
    }

    override fun toString(): String =
        "temperature: { value: ${"%.2f".format(value)}, unit: \"C\" }"
}

fun TemperatureItem.isEmpty(): Boolean =
    value.isNaN()

fun TemperatureItem?.isNullOrEmpty(): Boolean =
    this == null || isEmpty()
