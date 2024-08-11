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
%Point = type { i32, i1, i32 }
define void @main() {
entry:
%2 = call %Point* @new_point()
call i1 @print_point(%Point* %2)
ret void
}
define %Point** @new_point() {
entry:
%4 = call i8* @malloc(i32 9)
call void @memset(i8* %4, i32 0, i32 9)
%6 = bitcast i8* %4 to %Point*
%8 = add i32 100, 0
%10 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 0
store i32 %8, i32* %10
%12 = add i1 1, 0
%14 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 1
store i1 %12, i1* %14
%16 = add i32 50, 0
%18 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 2
store i32 %16, i32* %18
ret %Point* %6
}
define void @print_point(%Point* %20) {
entry:
%22 = getelementptr inbounds %Point, %Point* %20, i32 0, i32 0
%24 = load i32, i32* %22
call void @println_i32(i32 %24)
%26 = getelementptr inbounds %Point, %Point* %20, i32 0, i32 1
%28 = load i1, i1* %26
call void @println_bool(i1 %28)
%30 = getelementptr inbounds %Point, %Point* %20, i32 0, i32 2
%32 = load i32, i32* %30
call void @println_i32(i32 %32)
ret void
}