package me.gabriel.selene.backend.llvm

import me.gabriel.ryujin.transcript.DefaultDragonIrTranscriber
import me.gabriel.ryujin.transcript.DragonIrTranscriber
import me.gabriel.selene.backend.common.SeleneCompilerBackend
import me.gabriel.selene.backend.common.SeleneCompilerModule
import me.gabriel.selene.backend.llvm.session.SeleneDragonCompilingSession

class DragonCompilerBackend: SeleneCompilerBackend {
    var irTranscriber: DragonIrTranscriber = DefaultDragonIrTranscriber()

    override fun compile(module: SeleneCompilerModule): String {
        // TODO: remove
        if (module.stdlib) return ""

        val session = SeleneDragonCompilingSession(module)
        val dragonModule = session.compile()
        return irTranscriber.transcribe(dragonModule)
    }
}