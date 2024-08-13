package me.gabriel.gwydion.llvm.struct

class TraitObject(
    val data: MemoryUnit,  // Pointer to the actual data
    val vtable: MemoryUnit // Pointer to the vtable
)

class VTable(
    val destructor: MemoryUnit, // Function pointer to destructor
    val size: Int,              // Size of the concrete type
    val align: Int,             // Alignment of the concrete type
    val methods: List<MemoryUnit> // List of function pointers to methods
)