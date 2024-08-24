@trait_622525062 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Collection_next
}>, align 8
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare double @sqrt(double)
declare double @asin(double)
declare double @tan(double)
declare double @cos(double)
declare double @sin(double)
declare i32 @array_len(i32*)
declare i32 @str_length(i8*)
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare i8* @readln()
    %Collection = type { [7 x i32]*, i32 }
define i32 @Collection_next(%Collection* %1) {
entry:
    %2 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 0
    %3 = load [7 x i32]*, [7 x i32]** %2
    %4 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    %5 = load i32, i32* %4
    %6 = getelementptr inbounds [7 x i32], [7 x i32]* %3, i32 0, i32 %5
    %7 = load i32, i32* %6
    %8 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    %9 = load i32, i32* %8
    %10 = add i32 %9, 1
    %11 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    store i32 %10, i32* %11
    ret i32 %7
}
define void @main() {
entry:
    %12 = alloca [7 x i32], align 8
    %13 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 0
    store i32 2, i32* %13
    %14 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 1
    store i32 3, i32* %14
    %15 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 2
    store i32 5, i32* %15
    %16 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 3
    store i32 7, i32* %16
    %17 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 4
    store i32 11, i32* %17
    %18 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 5
    store i32 13, i32* %18
    %19 = getelementptr inbounds [7 x i32], [7 x i32]* %12, i32 0, i32 6
    store i32 17, i32* %19
    %20 = alloca %Collection, align 8
    %21 = getelementptr inbounds %Collection, %Collection* %20, i32 0, i32 0
    store [7 x i32]* %12, [7 x i32]** %21
    %22 = getelementptr inbounds %Collection, %Collection* %20, i32 0, i32 1
    store i32 0, i32* %22
    %23 = alloca i32, align 4
    store i32 0, i32* %23
    %24 = add i32 7, 0
    %25 = sub i32 %24, 1
    br label %label0
label0:
    %27 = load i32, i32* %23
    %28 = icmp sle i32 %27, %25
    br i1 %28, label %label1, label %label2
label1:
    %29 = load i32, i32* %23
    %30 = alloca [15 x i8], align 8
    %31 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 0
    store i8 78, i8* %31
    %32 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 1
    store i8 111, i8* %32
    %33 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 2
    store i8 119, i8* %33
    %34 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 3
    store i8 32, i8* %34
    %35 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 4
    store i8 97, i8* %35
    %36 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 5
    store i8 116, i8* %36
    %37 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 6
    store i8 32, i8* %37
    %38 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 7
    store i8 105, i8* %38
    %39 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 8
    store i8 110, i8* %39
    %40 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 9
    store i8 100, i8* %40
    %41 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 10
    store i8 101, i8* %41
    %42 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 11
    store i8 120, i8* %42
    %43 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 12
    store i8 58, i8* %43
    %44 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 13
    store i8 32, i8* %44
    %45 = getelementptr inbounds [15 x i8], [15 x i8]* %30, i32 0, i32 14
    store i8 0, i8* %45
    call i1 @printf([15 x i8]* %30)
    call void @println_i32(i32 %29)
    %48 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_622525062, i32 0, i32 2
    %49 = load ptr, ptr %48
    %50 = call i32 %49(%Collection* %20)
    call void @println_i32(i32 %50)
    %52 = add i32 %27, 1
    store i32 %52, i32* %23
    br label %label0
label2:
    ret void
}