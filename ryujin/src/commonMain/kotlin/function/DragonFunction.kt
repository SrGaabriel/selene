package me.gabriel.ryujin.function

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.statement.DragonStatement
import me.gabriel.ryujin.struct.DragonType

class DragonFunction(
    val module: DragonModule,
    val name: String,
    val parameters: Map<String, DragonType>,
    val returnType: DragonType
) {
    val statements = mutableListOf<DragonStatement>()
}