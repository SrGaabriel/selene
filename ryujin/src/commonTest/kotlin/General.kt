import me.gabriel.ryujin.dsl.ryujinModule
import me.gabriel.ryujin.struct.Constant
import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.transcript.DefaultDragonIrTranscriber
import kotlin.test.Test

class GeneralTest {

    val transcriber = DefaultDragonIrTranscriber()

    @Test
    fun functionSignature() {
        val module = ryujinModule {
            function("main", DragonType.Int32, listOf(
                DragonType.Int32,
                DragonType.Pointer(DragonType.Pointer(DragonType.Int8))
            )) {}
        }
        val transcribed = transcriber.transcribe(module)
        assertModuleEquals(
            """
            |define i32 @main(i32 %0, i8** %1) {
            |}
            """,
            transcribed
        )
    }

    @Test
    fun basicMemory() {
        val module = ryujinModule {
            function("main", DragonType.Int32) { arguments ->
                val three = assign {
                    add(Constant.Number("2", DragonType.Int32), Constant.Number("1", DragonType.Int32))
                }
            }
        }
        val transcribed = transcriber.transcribe(module)
        assertModuleEquals(
            """
            |define i32 @main() {
            |  %1 = add i32 2, 1
            |}
            """,
            transcribed
        )
    }

    @Test
    fun virtualTable() {
        val module = ryujinModule {
            virtualTable(
                "my_vtable", listOf(
                    Constant.DeclaredConstantPtr("test"),
                    Constant.DeclaredConstantPtr("test2")
                )
            )
        }
        val transcribed = transcriber.transcribe(module)
        assertModuleEquals(
            """
            |@my_vtable = unnamed_addr constant <{ ptr, ptr }> <{
            |  ptr @test,
            |  ptr @test2
            |}>
            """,
            transcribed
        )
    }

    private inline fun assertModuleEquals(expected: String, actual: String) {
        val formattedExpected = expected.trimMargin().trim()
        val formattedActual = actual.trim()
        if (formattedExpected != formattedActual) {
            error("Expected:\n$formattedExpected\nActual:\n$formattedActual")
        }
    }
}