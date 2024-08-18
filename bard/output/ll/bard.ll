@trait_548161757 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_fooat
}>, align 8
@trait_1264273593 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
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
%11 = alloca [7 x i8], align 1
%12 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 0
store i8 115, i8* %12
%13 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 1
store i8 116, i8* %13
%14 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 2
store i8 114, i8* %14
%15 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 3
store i8 105, i8* %15
%16 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 4
store i8 110, i8* %16
%17 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 5
store i8 103, i8* %17
%18 = getelementptr inbounds [7 x i8], [7 x i8]* %11, i32 0, i32 6
store i8 0, i8* %18
call void @println_str(i8* %12)
%20 = call i32 @str_length(i8** %10)
ret i32 %20
}
define void @main() {
entry:
%21 = call i8* @malloc(i32 4)
call void @memset(i8* %21, i32 0, i32 4)
%22 = bitcast i8* %21 to %Map*
%23 = getelementptr inbounds %Map, %Map* %22, i32 0, i32 0
store i32 720, i32* %23
%24 = alloca [5 x i8], align 1
%25 = getelementptr inbounds [5 x i8], [5 x i8]* %24, i32 0, i32 0
store i8 119, i8* %25
%26 = getelementptr inbounds [5 x i8], [5 x i8]* %24, i32 0, i32 1
store i8 111, i8* %26
%27 = getelementptr inbounds [5 x i8], [5 x i8]* %24, i32 0, i32 2
store i8 119, i8* %27
%28 = getelementptr inbounds [5 x i8], [5 x i8]* %24, i32 0, i32 3
store i8 111, i8* %28
%29 = getelementptr inbounds [5 x i8], [5 x i8]* %24, i32 0, i32 4
store i8 0, i8* %29
call i1 @print_fooable(ptr @trait_548161757, i8* %25)
call i1 @print_fooable(ptr @trait_1264273593, %Map* %22)
ret void
}
define void @print_fooable(ptr %32, ptr %33) {
entry:
%34 = getelementptr inbounds <{i16, i16, ptr}>, ptr %32, i32 0, i32 2
%35 = load ptr, ptr %34
%36 = call i32 %35(ptr %33)
call void @println_i32(i32 %36)
ret void
}
define void @do_something(ptr %38, ptr %39) {
entry:
%40 = getelementptr inbounds <{i16, i16, ptr}>, ptr %38, i32 0, i32 2
%41 = load ptr, ptr %40
%42 = call i32 %41(ptr %39)
call void @println_i32(i32 %42)
ret void
}