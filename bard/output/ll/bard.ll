@trait_745172384 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_fooat
}>, align 8
@trait_532330330 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
@trait_260692249 = external constant <{ i16, i16, ptr }>
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare double @sqrt(double)
declare double @asin(double)
declare double @tan(double)
declare double @cos(double)
declare double @sin(double)
declare i32 @str_length(i8*)
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare i8* @readln()
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
%11 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_260692249, i32 0, i32 2
%12 = load ptr, ptr %11
%13 = call i32 %12(i8** %10)
%14 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_260692249, i32 0, i32 2
%15 = load ptr, ptr %14
%16 = call i32 %15(i8** %10)
%17 = add i32 %16, 3
ret i32 %17
}
define void @main() {
entry:
%18 = call i8* @malloc(i32 4)
call void @memset(i8* %18, i32 0, i32 4)
%19 = bitcast i8* %18 to %Map*
%20 = getelementptr inbounds %Map, %Map* %19, i32 0, i32 0
store i32 720, i32* %20
%21 = alloca [5 x i8], align 1
%22 = getelementptr inbounds [5 x i8], [5 x i8]* %21, i32 0, i32 0
store i8 119, i8* %22
%23 = getelementptr inbounds [5 x i8], [5 x i8]* %21, i32 0, i32 1
store i8 111, i8* %23
%24 = getelementptr inbounds [5 x i8], [5 x i8]* %21, i32 0, i32 2
store i8 119, i8* %24
%25 = getelementptr inbounds [5 x i8], [5 x i8]* %21, i32 0, i32 3
store i8 111, i8* %25
%26 = getelementptr inbounds [5 x i8], [5 x i8]* %21, i32 0, i32 4
store i8 0, i8* %26
%27 = call i32 @test()
call i1 @print_fooable(ptr @trait_745172384, i8* %22)
call i1 @print_fooable(ptr @trait_532330330, %Map* %19)
%30 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_260692249, i32 0, i32 2
%31 = load ptr, ptr %30
%32 = call i32 %31(i8* %22)
call void @println_i32(i32 %32)
%34 = alloca i32, align 4
store i32 0, i32* %34
%35 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_260692249, i32 0, i32 2
%36 = load ptr, ptr %35
%37 = call i32 %36(i8* %22)
br label %label0
label0:
%39 = load i32, i32* %34
%40 = icmp ne i32 %39, %37
br i1 %40, label %label1, label %label2
label1:
%41 = load i32, i32* %34
call void @println_i32(i32 %41)
%43 = add i32 %39, 1
store i32 %43, i32* %34
br label %label0
label2:
ret void
}
define i32 @test() {
entry:
%45 = add i32 5, 0
ret i32 %45
}
define void @print_fooable(ptr %46, ptr %47) {
entry:
%48 = getelementptr inbounds <{i16, i16, ptr}>, ptr %46, i32 0, i32 2
%49 = load ptr, ptr %48
%50 = call i32 %49(ptr %47)
call void @println_i32(i32 %50)
ret void
}
define void @do_something(ptr %52, ptr %53) {
entry:
%54 = getelementptr inbounds <{i16, i16, ptr}>, ptr %52, i32 0, i32 2
%55 = load ptr, ptr %54
%56 = call i32 %55(ptr %53)
call void @println_i32(i32 %56)
%58 = fadd double 2.0, 0.0
%59 = call double @cos(double %58)
%60 = fadd double 2.0, 0.0
%61 = call double @sin(double %60)
%62 = fmul double %59, %61
call void @println_f64(double %62)
ret void
}