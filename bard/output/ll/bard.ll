declare i8* @strcat(i8*, i8*)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
declare i32 @strcmp(i8*, i8*)
declare i8* @readln()
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
define void @main() {
entry:
    call i1 @check_typed()
    ret void
}
define void @check_if_typed_apple(i8* %2) {
entry:
    %3 = alloca [6 x i8], align 1
    %4 = getelementptr inbounds [6 x i8], [6 x i8]* %3, i32 0, i32 0
    store i8 97, i8* %4
    %5 = getelementptr inbounds [6 x i8], [6 x i8]* %3, i32 0, i32 1
    store i8 112, i8* %5
    %6 = getelementptr inbounds [6 x i8], [6 x i8]* %3, i32 0, i32 2
    store i8 112, i8* %6
    %7 = getelementptr inbounds [6 x i8], [6 x i8]* %3, i32 0, i32 3
    store i8 108, i8* %7
    %8 = getelementptr inbounds [6 x i8], [6 x i8]* %3, i32 0, i32 4
    store i8 101, i8* %8
    %9 = getelementptr inbounds [6 x i8], [6 x i8]* %3, i32 0, i32 5
    store i8 0, i8* %9
    %10 = call i32 @strcmp(i8* %2, i8* %3)
    %11 = icmp eq i32 %10, 0
    br i1 %11, label %label0, label %label1
label0:
    %12 = alloca [18 x i8], align 1
    %13 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 0
    store i8 89, i8* %13
    %14 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 1
    store i8 111, i8* %14
    %15 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 2
    store i8 117, i8* %15
    %16 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 3
    store i8 32, i8* %16
    %17 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 4
    store i8 116, i8* %17
    %18 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 5
    store i8 121, i8* %18
    %19 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 6
    store i8 112, i8* %19
    %20 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 7
    store i8 101, i8* %20
    %21 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 8
    store i8 100, i8* %21
    %22 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 9
    store i8 32, i8* %22
    %23 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 10
    store i8 39, i8* %23
    %24 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 11
    store i8 97, i8* %24
    %25 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 12
    store i8 112, i8* %25
    %26 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 13
    store i8 112, i8* %26
    %27 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 14
    store i8 108, i8* %27
    %28 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 15
    store i8 101, i8* %28
    %29 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 16
    store i8 39, i8* %29
    %30 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 17
    store i8 0, i8* %30
    %31 = getelementptr inbounds [18 x i8], [18 x i8]* %12, i32 0, i32 0
    call i1 @println_str(i8* %31)
    br label %label2
label1:
    %35 = alloca [25 x i8], align 1
    %36 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 0
    store i8 89, i8* %36
    %37 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 1
    store i8 111, i8* %37
    %38 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 2
    store i8 117, i8* %38
    %39 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 3
    store i8 32, i8* %39
    %40 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 4
    store i8 100, i8* %40
    %41 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 5
    store i8 105, i8* %41
    %42 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 6
    store i8 100, i8* %42
    %43 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 7
    store i8 110, i8* %43
    %44 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 8
    store i8 39, i8* %44
    %45 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 9
    store i8 116, i8* %45
    %46 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 10
    store i8 32, i8* %46
    %47 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 11
    store i8 116, i8* %47
    %48 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 12
    store i8 121, i8* %48
    %49 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 13
    store i8 112, i8* %49
    %50 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 14
    store i8 101, i8* %50
    %51 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 15
    store i8 32, i8* %51
    %52 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 16
    store i8 39, i8* %52
    %53 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 17
    store i8 97, i8* %53
    %54 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 18
    store i8 112, i8* %54
    %55 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 19
    store i8 112, i8* %55
    %56 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 20
    store i8 108, i8* %56
    %57 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 21
    store i8 101, i8* %57
    %58 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 22
    store i8 39, i8* %58
    %59 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 23
    store i8 46, i8* %59
    %60 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 24
    store i8 0, i8* %60
    %61 = getelementptr inbounds [25 x i8], [25 x i8]* %35, i32 0, i32 0
    call i1 @println_str(i8* %61)
    call i1 @check_typed()
    br label %label2
label2:
    ret void
}
define void @check_typed() {
entry:
    %67 = call i8* @malloc(i32 64)
    call void @memset(i8* %67, i32 0, i32 64)
    call i1 @println_str(i8* %67)
    %70 = alloca [17 x i8], align 1
    %71 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 0
    store i8 84, i8* %71
    %72 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 1
    store i8 121, i8* %72
    %73 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 2
    store i8 112, i8* %73
    %74 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 3
    store i8 101, i8* %74
    %75 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 4
    store i8 32, i8* %75
    %76 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 5
    store i8 115, i8* %76
    %77 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 6
    store i8 111, i8* %77
    %78 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 7
    store i8 109, i8* %78
    %79 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 8
    store i8 101, i8* %79
    %80 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 9
    store i8 116, i8* %80
    %81 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 10
    store i8 104, i8* %81
    %82 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 11
    store i8 105, i8* %82
    %83 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 12
    store i8 110, i8* %83
    %84 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 13
    store i8 103, i8* %84
    %85 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 14
    store i8 58, i8* %85
    %86 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 15
    store i8 32, i8* %86
    %87 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 16
    store i8 0, i8* %87
    %88 = getelementptr inbounds [17 x i8], [17 x i8]* %70, i32 0, i32 0
    call i1 @printf(i8* %88)
    %91 = call i8* @readln()
    %92 = alloca [11 x i8], align 1
    %93 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 0
    store i8 89, i8* %93
    %94 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 1
    store i8 111, i8* %94
    %95 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 2
    store i8 117, i8* %95
    %96 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 3
    store i8 32, i8* %96
    %97 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 4
    store i8 116, i8* %97
    %98 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 5
    store i8 121, i8* %98
    %99 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 6
    store i8 112, i8* %99
    %100 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 7
    store i8 101, i8* %100
    %101 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 8
    store i8 100, i8* %101
    %102 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 9
    store i8 32, i8* %102
    %103 = getelementptr inbounds [11 x i8], [11 x i8]* %92, i32 0, i32 10
    store i8 0, i8* %103
    %104 = call i8* @malloc(i32 64)
    call void @memset(i8* %104, i32 0, i32 64)
    call i8* @strcat(i8* %104, i8* %92)
    call i8* @strcat(i8* %104, i8* %91)
    call i1 @println_str(i8* %104)
    call i1 @check_if_typed_apple(i8* %91)
    ret void
}