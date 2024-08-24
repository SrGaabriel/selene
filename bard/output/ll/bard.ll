declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@trait_1621295027 = external constant <{ i16, i16, ptr }>
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
%Collection = type { i8**, i32 }
define void @main() {
entry:
    %0 = alloca [8 x i8], align 1
    %1 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 0
    store i8 71, i8* %1
    %2 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 1
    store i8 119, i8* %2
    %3 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 2
    store i8 121, i8* %3
    %4 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 3
    store i8 100, i8* %4
    %5 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 4
    store i8 105, i8* %5
    %6 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 5
    store i8 111, i8* %6
    %7 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 6
    store i8 110, i8* %7
    %8 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 7
    store i8 0, i8* %8
    %9 = alloca [3 x i8], align 1
    %10 = getelementptr inbounds [3 x i8], [3 x i8]* %9, i32 0, i32 0
    store i8 73, i8* %10
    %11 = getelementptr inbounds [3 x i8], [3 x i8]* %9, i32 0, i32 1
    store i8 115, i8* %11
    %12 = getelementptr inbounds [3 x i8], [3 x i8]* %9, i32 0, i32 2
    store i8 0, i8* %12
    %13 = alloca [4 x i8], align 1
    %14 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 0
    store i8 84, i8* %14
    %15 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 1
    store i8 104, i8* %15
    %16 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 2
    store i8 101, i8* %16
    %17 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 3
    store i8 0, i8* %17
    %18 = alloca [6 x i8], align 1
    %19 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 0
    store i8 66, i8* %19
    %20 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 1
    store i8 101, i8* %20
    %21 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 2
    store i8 115, i8* %21
    %22 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 3
    store i8 116, i8* %22
    %23 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 4
    store i8 33, i8* %23
    %24 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 5
    store i8 0, i8* %24
    %25 = call i8* @malloc(i32 64)
    call void @memset(i8* %25, i32 0, i32 64)
    %26 = bitcast i8* %25 to i8**
    %27 = getelementptr inbounds i8*, i8** %26, i32 0
    store [8 x i8]* %0, i8** %27
    %28 = getelementptr inbounds i8*, i8** %26, i32 1
    store [3 x i8]* %9, i8** %28
    %29 = getelementptr inbounds i8*, i8** %26, i32 2
    store [4 x i8]* %13, i8** %29
    %30 = getelementptr inbounds i8*, i8** %26, i32 3
    store [6 x i8]* %18, i8** %30
    %32 = alloca %Collection, align 8
    %33 = getelementptr inbounds %Collection, %Collection* %32, i32 0, i32 0
    store i8** %26, i8*** %33
    %34 = getelementptr inbounds %Collection, %Collection* %32, i32 0, i32 1
    store i32 0, i32* %34
    %35 = alloca i32, align 4
    store i32 0, i32* %35
    br label %label0
label0:
    %37 = load i32, i32* %35
    %38 = icmp sle i32 %37, 3
    br i1 %38, label %label1, label %label2
label1:
    %39 = load i32, i32* %35
    %40 = alloca [7 x i8], align 1
    %41 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 0
    store i8 78, i8* %41
    %42 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 1
    store i8 101, i8* %42
    %43 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 2
    store i8 120, i8* %43
    %44 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 3
    store i8 116, i8* %44
    %45 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 4
    store i8 58, i8* %45
    %46 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 5
    store i8 32, i8* %46
    %47 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 6
    store i8 0, i8* %47
    %48 = getelementptr inbounds [7 x i8], [7 x i8]* %40, i32 0, i32 0
    call i1 @printf(i8* %48)
    %50 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_1621295027, i32 0, i32 2
    %51 = load ptr, ptr %50
    %52 = call i8* %51(%Collection* %32)
    call void @println_str(i8* %52)
    %54 = add i32 %37, 1
    store i32 %54, i32* %35
    br label %label0
label2:
    ret void
}