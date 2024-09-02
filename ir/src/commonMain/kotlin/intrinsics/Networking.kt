package me.gabriel.gwydion.ir.intrinsics

class SocketFunction : IntrinsicMirrorFunction(
    name = "socket",
    params = "i32, i32, i32"
)

class SocketBindFunction : IntrinsicMirrorFunction(
    name = "bind",
    params = "i32, i8*, i32"
)

class SocketListenFunction : IntrinsicMirrorFunction(
    name = "listen",
    params = "i32, i32"
)

class SocketAcceptFunction : IntrinsicMirrorFunction(
    name = "accept",
    params = "i32, i8*, i32"
)

class SocketReceiveFunction : IntrinsicMirrorFunction(
    name = "receive",
    llvmName = "recv",
    params = "i32, i8*, i32, i32"
)

class SocketSendFunction : IntrinsicMirrorFunction(
    name = "send",
    params = "i32, i8*, i32, i32"
)

class SocketCloseFunction : IntrinsicMirrorFunction(
    name = "close",
    params = "i32"
)