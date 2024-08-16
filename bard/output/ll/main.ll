            @trait_2 = private unnamed_addr constant <{ i16, i16, ptr, ptr }> <{
                i16 8,
                i16 8,
                ptr @Point_area, 
ptr @Point_perimeter
            }>, align 8
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
%Point = type { i32, i32 }
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
store i32 0, i32* %38
%40 = getelementptr inbounds %Point, %Point* %36, i32 0, i32 1
store i32 0, i32* %40
%42 = getelementptr inbounds {i16, i16, ptr, ptr}, ptr @trait_2, i32 0, i32 2
%44 = load ptr, ptr %42
%46 = bitcast ptr %44 to i32 (%Point*)*
%48 = call i32 %46(%Point* %36)
%50 = add i32 5, 0
call void @println_i32(i32 %50)
ret void
}
define i32 @Point_area(%Point* %52) {
entry:
%54 = alloca [14 x i8], align 1
%56 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 0
store i8 65, i8* %56
%58 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 1
store i8 114, i8* %58
%60 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 2
store i8 101, i8* %60
%62 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 3
store i8 97, i8* %62
%64 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 4
store i8 32, i8* %64
%66 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 5
store i8 111, i8* %66
%68 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 6
store i8 102, i8* %68
%70 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 7
store i8 32, i8* %70
%72 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 8
store i8 80, i8* %72
%74 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 9
store i8 111, i8* %74
%76 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 10
store i8 105, i8* %76
%78 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 11
store i8 110, i8* %78
%80 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 12
store i8 116, i8* %80
%82 = getelementptr inbounds [14 x i8], [14 x i8]* %54, i32 0, i32 13
store i8 0, i8* %82
call void @println_str(i8* %56)
ret i32 0
}
define i32 @Point_perimeter(%Point* %84) {
entry:
ret i32 0
}