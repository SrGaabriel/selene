import kotlinx.serialization.json.Json
import me.gabriel.gwydion.compiler.GwydionCompilerPlatform
import me.gabriel.gwydion.compiler.io.IoPlatform
import me.gabriel.gwydion.tools.GwydionLogger
import okio.FileSystem

class NativeCompilerPlatform(
    override val logger: GwydionLogger,
): GwydionCompilerPlatform {
    override val io: IoPlatform = Io

    override fun exitProcess(status: Int): Nothing {
        kotlin.system.exitProcess(status)
    }

    companion object Io : IoPlatform {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }

        override fun getJson(): Json = json

        override fun getFileSystem(): FileSystem = FileSystem.SYSTEM
    }
}