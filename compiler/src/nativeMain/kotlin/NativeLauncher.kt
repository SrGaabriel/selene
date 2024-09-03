import kotlinx.serialization.json.Json
import me.gabriel.gwydion.compiler.GwydionCompiler
import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.compiler.log.MordantLogger

fun main(args: Array<String>) {
    val platform = NativeCompilerPlatform(
        logger = MordantLogger(),
        cli = CommandLine(args),
        json = Json { encodeDefaults = false }
    )
    val compiler = GwydionCompiler(platform)
    compiler.start()
}