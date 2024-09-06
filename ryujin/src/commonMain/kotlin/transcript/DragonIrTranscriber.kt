package me.gabriel.ryujin.transcript

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.struct.Dependency

interface DragonIrTranscriber {
    fun transcribe(module: DragonModule): String

    fun transcribeDependency(dependency: Dependency): String

    fun transcribeFunction(function: DragonFunction): String
}