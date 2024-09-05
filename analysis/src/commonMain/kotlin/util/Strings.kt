package me.gabriel.selene.analysis.util

import me.gabriel.selene.frontend.SeleneType

fun String.castToType(type: SeleneType): String = when (type) {
    SeleneType.Int8 -> this.toByte().toString()
    SeleneType.Int16 -> this.toShort().toString()
    SeleneType.Int32 -> this.toInt().toString()
    SeleneType.Int64 -> this.toLong().toString()
    SeleneType.Float32 -> this.toFloat().toString()
    SeleneType.Float64 -> this.toDouble().toString()
    else -> this
}