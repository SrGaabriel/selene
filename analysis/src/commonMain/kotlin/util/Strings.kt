package me.gabriel.gwydion.analysis.util

import me.gabriel.gwydion.frontend.GwydionType

fun String.castToType(type: GwydionType): String = when (type) {
    GwydionType.Int8 -> this.toByte().toString()
    GwydionType.Int16 -> this.toShort().toString()
    GwydionType.Int32 -> this.toInt().toString()
    GwydionType.Int64 -> this.toLong().toString()
    GwydionType.Float32 -> this.toFloat().toString()
    GwydionType.Float64 -> this.toDouble().toString()
    else -> this
}