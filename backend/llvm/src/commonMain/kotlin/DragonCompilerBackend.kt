package me.gabriel.selene.backend.llvm

import me.gabriel.ryujin.struct.Value
import me.gabriel.ryujin.transcript.DefaultDragonIrTranscriber
import me.gabriel.ryujin.transcript.DragonIrTranscriber
import me.gabriel.selene.backend.common.SeleneCompilerBackend
import me.gabriel.selene.backend.common.SeleneCompilerModule
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionRepository
import me.gabriel.selene.backend.llvm.intrinsic.DragonIntrinsicFunctionRepository
import me.gabriel.selene.backend.llvm.session.SeleneDragonCompilingSession

class DragonCompilerBackend: SeleneCompilerBackend<DragonHookContext, Value> {
    var irTranscriber: DragonIrTranscriber = DefaultDragonIrTranscriber()

    override val intrinsics: IntrinsicFunctionRepository<DragonHookContext, Value> = DragonIntrinsicFunctionRepository()

    override fun compile(module: SeleneCompilerModule): String {
        val session = SeleneDragonCompilingSession(
            module,
            intrinsics
        )
        val dragonModule = session.compile()
        return irTranscriber.transcribe(dragonModule)
    }
}