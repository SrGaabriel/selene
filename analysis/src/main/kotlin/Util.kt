package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.Type

fun String.castToType(type: Type): String = when (type) {
    Type.Int8 -> this.toByte().toString()
    Type.Int16 -> this.toShort().toString()
    Type.Int32 -> this.toInt().toString()
    Type.Int64 -> this.toLong().toString()
    Type.Float32 -> this.toFloat().toString()
    Type.Float64 -> this.toDouble().toString()
    else -> this
}