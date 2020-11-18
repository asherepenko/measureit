package com.sherepenko.android.measureit.data

data class PressureItem(
    val value: Double
) {
    companion object {
        const val MIN_VALUE = 5.0 // Bar
        const val MAX_VALUE = 200.0 // Bar

        fun empty(): PressureItem =
            PressureItem(Double.NaN)
    }

    override fun toString(): String =
        "pressure: { value: ${"%.2f".format(value)}, unit: \"Bar\" }"
}

fun PressureItem.isEmpty(): Boolean =
    value.isNaN()

fun PressureItem?.isNullOrEmpty(): Boolean =
    this == null || isEmpty()
