@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare i8* @test()
%Point = type { i32, i32 }
%trait.Shape = type { i32 ()*, i32 ()* }
%vtable.Shape.Point = type { i32 ()*, i32 ()* }
%6 = getelementptr inbounds %Point, %Point* %0, i32 0, i32 0
store %vtable.Shape.Point %4, %vtable.Shape.Point** %6