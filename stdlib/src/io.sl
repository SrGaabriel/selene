trait Legible(
    text(self) :: string
)

intrinsic func printf (message: any) {}

intrinsic func println (message: any) {}

intrinsic func readln () :: string {}

intrinsic func arraylen (arr: any) :: int32 {}