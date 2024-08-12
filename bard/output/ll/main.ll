@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
%Point = type { i32, i32, i1 }
define void @main() {
entry:
%2 = call %Point* @new_point()
call i1 @print_point(%Point* %2)
call i1 @change_point(%Point* %2)
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
%12 = add i32 400, 0
%14 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 1
store i32 %12, i32* %14
%16 = add i1 1, 0
%18 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 2
store i1 %16, i1* %18
ret %Point* %6
}
define void @change_point(%Point* %20) {
entry:
%22 = add i32 200, 0
%24 = getelementptr inbounds %Point, %Point* %20, i32 0, i32 0
store i32 %22, i32* %24
%26 = add i32 800, 0
%28 = getelementptr inbounds %Point, %Point* %20, i32 0, i32 1
store i32 %26, i32* %28
%30 = add i1 0, 0
%32 = getelementptr inbounds %Point, %Point* %20, i32 0, i32 2
store i1 %30, i1* %32
ret void
}
define void @print_point(%Point* %34) {
entry:
%36 = getelementptr inbounds %Point, %Point* %34, i32 0, i32 0
%38 = load i32, i32* %36
call void @println_i32(i32 %38)
%40 = getelementptr inbounds %Point, %Point* %34, i32 0, i32 1
%42 = load i32, i32* %40
call void @println_i32(i32 %42)
%44 = getelementptr inbounds %Point, %Point* %34, i32 0, i32 2
%46 = load i1, i1* %44
call void @println_bool(i1 %46)
ret void
}