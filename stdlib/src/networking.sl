data Socket(
    id: int32,
    family: int32,
    port: int16,
    ip_address: int32
)

data TcpServer(
    ip_address: int32,
    port: int16
)

internal intrinsic func socket (family: int32, type: int32, protocol: int32) :: int32 {}

internal intrinsic func bind (socket: int32, addr: int8, addrlen: int32) :: int32 {}

internal intrinsic func listen (socket: int32, backlog: int32) :: int32 {}

internal intrinsic func accept (socket: int32, addr: int8, addrlen: int32) :: int32 {}

internal intrinsic func receive (client: int32, buffer: int8, length: int32, flags: int32) :: int32 {}

internal intrinsic func send (client: int32, buffer: int8, length: int32, flags: int32) :: int32 {}

internal intrinsic func close (socket: int32) :: int32 {}
