package me.gabriel.selene.ir.intrinsics

class SinFunction: IntrinsicMirrorFunction(
    name = "sin",
    llvmName = "sin",
    params = "double"
)

class CosFunction: IntrinsicMirrorFunction(
    name = "cos",
    llvmName = "cos",
    params = "double"
)

class TanFunction: IntrinsicMirrorFunction(
    name = "tan",
    llvmName = "tan",
    params = "double"
)

class AsinFunction: IntrinsicMirrorFunction(
    name = "asin",
    llvmName = "asin",
    params = "double"
)

class AcosFunction: IntrinsicMirrorFunction(
    name = "acos",
    llvmName = "acos",
    params = "double"
)

class AtanFunction: IntrinsicMirrorFunction(
    name = "atan",
    llvmName = "atan",
    params = "double"
)

class Atan2Function: IntrinsicMirrorFunction(
    name = "atan2",
    llvmName = "atan2",
    params = "double, double"
)

class SqrtFunction: IntrinsicMirrorFunction(
    name = "sqrt",
    llvmName = "sqrt",
    params = "double"
)