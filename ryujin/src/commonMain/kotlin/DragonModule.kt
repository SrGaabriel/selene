package me.gabriel.ryujin

import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.struct.Dependency

// Yes, I will be using the dragon prefix.
// Blame camelCase/PascalCase for being so shitty. (LLVMModule... seriously?)
interface DragonModule {
    val functions: MutableSet<DragonFunction>
    val dependencies: MutableSet<Dependency>

    fun addDependency(dependency: Dependency) {
        dependencies.add(dependency)
    }

    fun addDependencies(vararg dependencies: Dependency) {
        this.dependencies.addAll(dependencies)
    }

    fun removeDependency(dependency: Dependency) {
        dependencies.remove(dependency)
    }

    fun removeDependencies(vararg dependencies: Dependency) {
        this.dependencies.removeAll(dependencies)
    }

    fun addFunction(function: DragonFunction) {
        functions.add(function)
    }

    fun addFunctions(vararg functions: DragonFunction) {
        this.functions.addAll(functions)
    }

    fun removeFunction(function: DragonFunction) {
        functions.remove(function)
    }

    fun removeFunctions(vararg functions: DragonFunction) {
        this.functions.removeAll(functions)
    }
}