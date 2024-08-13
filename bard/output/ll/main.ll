@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare i8* @test()
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
%Point = type { i32, i32, i1 }
%Map = type { %Point* }
define void @main() {
entry:
%4 = call %Point* @new_point()
call i1 @print_point(%Point* %4)
call i1 @change_point(%Point* %4)
call i1 @print_point(%Point* %4)
ret void
}
define %Point** @new_point() {
entry:
%6 = call i8* @malloc(i32 9)
call void @memset(i8* %6, i32 0, i32 9)
%8 = bitcast i8* %6 to %Point*
%10 = add i32 100, 0
%12 = getelementptr inbounds %Point, %Point* %8, i32 0, i32 0
store i32 %10, i32* %12
%14 = add i32 400, 0
%16 = getelementptr inbounds %Point, %Point* %8, i32 0, i32 1
store i32 %14, i32* %16
%18 = add i1 1, 0
%20 = getelementptr inbounds %Point, %Point* %8, i32 0, i32 2
store i1 %18, i1* %20
ret %Point* %8
}
define void @change_point(%Point* %22) {
entry:
%24 = add i32 200, 0
%26 = getelementptr inbounds %Point, %Point* %22, i32 0, i32 0
store i32 %24, i32* %26
%28 = add i32 800, 0
%30 = getelementptr inbounds %Point, %Point* %22, i32 0, i32 1
store i32 %28, i32* %30
%32 = add i1 0, 0
%34 = getelementptr inbounds %Point, %Point* %22, i32 0, i32 2
store i1 %32, i1* %34
ret void
}
define void @print_point(%Point* %36) {
entry:
%38 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 0
%40 = load i32, i32* %38
call void @println_i32(i32 %40)
%42 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 1
%44 = load i32, i32* %42
call void @println_i32(i32 %44)
%46 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 2
%48 = load i1, i1* %46
call void @println_bool(i1 %48)
ret void
}