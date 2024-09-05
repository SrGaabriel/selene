import me.gabriel.gwydion.compiler.GwydionCompiler
import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.compiler.log.MordantLogger

fun main(args: Array<String>) {
    val platform = NativeCompilerPlatform(
        logger = MordantLogger()
    )
    val compiler = GwydionCompiler(
        platform = platform,
        cli = CommandLine(args)
    )
    compiler.start()
}