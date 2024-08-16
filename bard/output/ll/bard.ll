@trait_2 = private unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
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
%Map = type { i32 }
define void @main() {
entry:
%4 = alloca [14 x i8], align 1
%6 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 0
store i8 72, i8* %6
%8 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 1
store i8 101, i8* %8
%10 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 2
store i8 108, i8* %10
%12 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 3
store i8 108, i8* %12
%14 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 4
store i8 111, i8* %14
%16 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 5
store i8 44, i8* %16
%18 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 6
store i8 32, i8* %18
%20 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 7
store i8 87, i8* %20
%22 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 8
store i8 111, i8* %22
%24 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 9
store i8 114, i8* %24
%26 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 10
store i8 108, i8* %26
%28 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 11
store i8 100, i8* %28
%30 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 12
store i8 33, i8* %30
%32 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 13
store i8 0, i8* %32
call void @println_str(i8* %6)
%34 = call i8* @malloc(i32 8)
call void @memset(i8* %34, i32 0, i32 8)
%36 = bitcast i8* %34 to %Point*
%38 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 0
store i32 6, i32* %38
%40 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 1
store i32 4, i32* %40
%42 = call i8* @malloc(i32 4)
call void @memset(i8* %42, i32 0, i32 4)
%44 = bitcast i8* %42 to %Map*
%46 = getelementptr inbounds %Map, %Map* %44, i32 0, i32 0
store i32 20, i32* %46
%48 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 0
%50 = load i32, i32* %48
call void @println_i32(i32 %50)
%52 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 1
%54 = load i32, i32* %52
call void @println_i32(i32 %54)
%56 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_2, i32 0, i32 2
%58 = load ptr, ptr %56
%60 = call i32 %58(%Map* %44)
call void @println_i32(i32 %60)
ret void
}
define i32 @Map_fooat(%Map* %62) {
entry:
%64 = getelementptr inbounds %Map, %Map* %62, i32 0, i32 0
%66 = load i32, i32* %64
ret i32 %66
}