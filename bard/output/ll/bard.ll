@trait_748586900 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
@trait_-1391584027 = external constant <{ i16, i16, ptr, ptr }>
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
define i32 @Map_fooat(%Map* %2) {
entry:
%4 = getelementptr inbounds %Map, %Map* %2, i32 0, i32 0
%6 = load i32, i32* %4
ret i32 %6
}
define void @main() {
entry:
%8 = alloca [14 x i8], align 1
%10 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 0
store i8 72, i8* %10
%12 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 1
store i8 101, i8* %12
%14 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 2
store i8 108, i8* %14
%16 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 3
store i8 108, i8* %16
%18 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 4
store i8 111, i8* %18
%20 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 5
store i8 44, i8* %20
%22 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 6
store i8 32, i8* %22
%24 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 7
store i8 87, i8* %24
%26 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 8
store i8 111, i8* %26
%28 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 9
store i8 114, i8* %28
%30 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 10
store i8 108, i8* %30
%32 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 11
store i8 100, i8* %32
%34 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 12
store i8 33, i8* %34
%36 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 13
store i8 0, i8* %36
call void @println_str(i8* %10)
%38 = call i8* @malloc(i32 8)
call void @memset(i8* %38, i32 0, i32 8)
%40 = bitcast i8* %38 to %Point*
%42 = getelementptr inbounds %Point, %Point* %40, i32 0, i32 0
store i32 24, i32* %42
%44 = getelementptr inbounds %Point, %Point* %40, i32 0, i32 1
store i32 4, i32* %44
%46 = getelementptr inbounds %Point, %Point* %40, i32 0, i32 0
%48 = load i32, i32* %46
call void @println_i32(i32 %48)
%50 = getelementptr inbounds %Point, %Point* %40, i32 0, i32 1
%52 = load i32, i32* %50
call void @println_i32(i32 %52)
%54 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_-1391584027, i32 0, i32 2
%56 = load ptr, ptr %54
%58 = call i32 %56(%Point* %40)
call void @println_i32(i32 %58)
%60 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_-1391584027, i32 0, i32 3
%62 = load ptr, ptr %60
%64 = call i32 %62(%Point* %40)
call void @println_i32(i32 %64)
%66 = call i8* @malloc(i32 4)
call void @memset(i8* %66, i32 0, i32 4)
%68 = bitcast i8* %66 to %Map*
%70 = getelementptr inbounds %Map, %Map* %68, i32 0, i32 0
store i32 42, i32* %70
%72 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_748586900, i32 0, i32 2
%74 = load ptr, ptr %72
%76 = call i32 %74(%Map* %68)
call void @println_i32(i32 %76)
ret void
}