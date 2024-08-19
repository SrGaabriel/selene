@trait_1766765120 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_fooat
}>, align 8
@trait_209185203 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
@trait_2028450666 = external constant <{ i16, i16, ptr }>
@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i32 @str_length(i8*)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
%Map = type { i32 }
define i32 @Map_fooat(%Map* %1) {
entry:
%2 = alloca [4 x i8], align 1
%3 = getelementptr inbounds [4 x i8], [4 x i8]* %2, i32 0, i32 0
store i8 77, i8* %3
%4 = getelementptr inbounds [4 x i8], [4 x i8]* %2, i32 0, i32 1
store i8 97, i8* %4
%5 = getelementptr inbounds [4 x i8], [4 x i8]* %2, i32 0, i32 2
store i8 112, i8* %5
%6 = getelementptr inbounds [4 x i8], [4 x i8]* %2, i32 0, i32 3
store i8 0, i8* %6
call void @println_str(i8* %3)
%8 = getelementptr inbounds %Map, %Map* %1, i32 0, i32 0
%9 = load i32, i32* %8
ret i32 %9
}
define i32 @string_fooat(i8** %10) {
entry:
%11 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_2028450666, i32 0, i32 2
%12 = load ptr, ptr %11
%13 = call i32 %12(i8** %10)
ret i32 %13
}
define void @main() {
entry:
%14 = call i8* @malloc(i32 4)
call void @memset(i8* %14, i32 0, i32 4)
%15 = bitcast i8* %14 to %Map*
%16 = getelementptr inbounds %Map, %Map* %15, i32 0, i32 0
store i32 720, i32* %16
%17 = alloca [5 x i8], align 1
%18 = getelementptr inbounds [5 x i8], [5 x i8]* %17, i32 0, i32 0
store i8 119, i8* %18
%19 = getelementptr inbounds [5 x i8], [5 x i8]* %17, i32 0, i32 1
store i8 111, i8* %19
%20 = getelementptr inbounds [5 x i8], [5 x i8]* %17, i32 0, i32 2
store i8 119, i8* %20
%21 = getelementptr inbounds [5 x i8], [5 x i8]* %17, i32 0, i32 3
store i8 111, i8* %21
%22 = getelementptr inbounds [5 x i8], [5 x i8]* %17, i32 0, i32 4
store i8 0, i8* %22
call i1 @test()
call i1 @print_fooable(ptr @trait_1766765120, i8* %18)
call i1 @print_fooable(ptr @trait_209185203, %Map* %15)
ret void
}
define i32 @test() {
entry:
%26 = add i32 5, 0
ret i32 %26
}
define void @print_fooable(ptr %27, ptr %28) {
entry:
%29 = getelementptr inbounds <{i16, i16, ptr}>, ptr %27, i32 0, i32 2
%30 = load ptr, ptr %29
%31 = call i32 %30(ptr %28)
call void @println_i32(i32 %31)
ret void
}
define void @do_something(ptr %33, ptr %34) {
entry:
%35 = getelementptr inbounds <{i16, i16, ptr}>, ptr %33, i32 0, i32 2
%36 = load ptr, ptr %35
%37 = call i32 %36(ptr %34)
call void @println_i32(i32 %37)
ret void
}