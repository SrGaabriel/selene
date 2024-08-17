@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
%Point = type { i32, i32 }
declare i8* @test()
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
@trait_2 = external constant <{ i16, i16, ptr, ptr }>
define void @main() {
entry:
%0 = alloca [14 x i8], align 1
%2 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 0
store i8 72, i8* %2
%4 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 1
store i8 101, i8* %4
%6 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 2
store i8 108, i8* %6
%8 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 3
store i8 108, i8* %8
%10 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 4
store i8 111, i8* %10
%12 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 5
store i8 44, i8* %12
%14 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 6
store i8 32, i8* %14
%16 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 7
store i8 87, i8* %16
%18 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 8
store i8 111, i8* %18
%20 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 9
store i8 114, i8* %20
%22 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 10
store i8 108, i8* %22
%24 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 11
store i8 100, i8* %24
%26 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 12
store i8 33, i8* %26
%28 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 13
store i8 0, i8* %28
call void @println_str(i8* %2)
%30 = call i8* @malloc(i32 8)
call void @memset(i8* %30, i32 0, i32 8)
%32 = bitcast i8* %30 to %Point*
%34 = getelementptr inbounds %Point, %Point* %32, i32 0, i32 0
store i32 6, i32* %34
%36 = getelementptr inbounds %Point, %Point* %32, i32 0, i32 1
store i32 4, i32* %36
%38 = getelementptr inbounds %Point, %Point* %32, i32 0, i32 0
%40 = load i32, i32* %38
call void @println_i32(i32 %40)
%42 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_2, i32 0, i32 2
%44 = load ptr, ptr %42
%46 = call i32 %44(%Point* %32)
call void @println_i32(i32 %46)
ret void
}