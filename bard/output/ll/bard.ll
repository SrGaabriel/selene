declare i32 @strcmp(i8*, i8*)
@trait_1720339 = external constant <{ i16, i16, ptr, ptr, ptr, ptr, ptr }>
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
%List = type { i8**, i32 }
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
define void @main() {
entry:
    %0 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 2
    %1 = load ptr, ptr %0
    %2 = call %List* %1()
    %3 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 3
    %4 = load ptr, ptr %3
    %5 = call i32 %4(%List* %2)
    call void @println_i32(i32 %5)
    %7 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 6
    %8 = load ptr, ptr %7
    %9 = alloca [6 x i8], align 1
    %10 = getelementptr inbounds [6 x i8], [6 x i8]* %9, i32 0, i32 0
    store i8 104, i8* %10
    %11 = getelementptr inbounds [6 x i8], [6 x i8]* %9, i32 0, i32 1
    store i8 101, i8* %11
    %12 = getelementptr inbounds [6 x i8], [6 x i8]* %9, i32 0, i32 2
    store i8 108, i8* %12
    %13 = getelementptr inbounds [6 x i8], [6 x i8]* %9, i32 0, i32 3
    store i8 108, i8* %13
    %14 = getelementptr inbounds [6 x i8], [6 x i8]* %9, i32 0, i32 4
    store i8 111, i8* %14
    %15 = getelementptr inbounds [6 x i8], [6 x i8]* %9, i32 0, i32 5
    store i8 0, i8* %15
    call i1 %8(%List* %2, [6 x i8]* %9)
    %17 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 6
    %18 = load ptr, ptr %17
    %19 = alloca [6 x i8], align 1
    %20 = getelementptr inbounds [6 x i8], [6 x i8]* %19, i32 0, i32 0
    store i8 104, i8* %20
    %21 = getelementptr inbounds [6 x i8], [6 x i8]* %19, i32 0, i32 1
    store i8 101, i8* %21
    %22 = getelementptr inbounds [6 x i8], [6 x i8]* %19, i32 0, i32 2
    store i8 108, i8* %22
    %23 = getelementptr inbounds [6 x i8], [6 x i8]* %19, i32 0, i32 3
    store i8 108, i8* %23
    %24 = getelementptr inbounds [6 x i8], [6 x i8]* %19, i32 0, i32 4
    store i8 111, i8* %24
    %25 = getelementptr inbounds [6 x i8], [6 x i8]* %19, i32 0, i32 5
    store i8 0, i8* %25
    call i1 %18(%List* %2, [6 x i8]* %19)
    %27 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 6
    %28 = load ptr, ptr %27
    %29 = alloca [6 x i8], align 1
    %30 = getelementptr inbounds [6 x i8], [6 x i8]* %29, i32 0, i32 0
    store i8 104, i8* %30
    %31 = getelementptr inbounds [6 x i8], [6 x i8]* %29, i32 0, i32 1
    store i8 101, i8* %31
    %32 = getelementptr inbounds [6 x i8], [6 x i8]* %29, i32 0, i32 2
    store i8 108, i8* %32
    %33 = getelementptr inbounds [6 x i8], [6 x i8]* %29, i32 0, i32 3
    store i8 108, i8* %33
    %34 = getelementptr inbounds [6 x i8], [6 x i8]* %29, i32 0, i32 4
    store i8 111, i8* %34
    %35 = getelementptr inbounds [6 x i8], [6 x i8]* %29, i32 0, i32 5
    store i8 0, i8* %35
    call i1 %28(%List* %2, [6 x i8]* %29)
    %37 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 3
    %38 = load ptr, ptr %37
    %39 = call i32 %38(%List* %2)
    call void @println_i32(i32 %39)
    %41 = alloca [6 x i8], align 1
    %42 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 0
    store i8 119, i8* %42
    %43 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 1
    store i8 111, i8* %43
    %44 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 2
    store i8 114, i8* %44
    %45 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 3
    store i8 108, i8* %45
    %46 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 4
    store i8 100, i8* %46
    %47 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 5
    store i8 0, i8* %47
    %48 = getelementptr inbounds [6 x i8], [6 x i8]* %41, i32 0, i32 0
    call void @println_str(i8* %48)
    %50 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 5
    %51 = load ptr, ptr %50
    %52 = call %List* %51(%List* %2, ptr @lambda_1278852808)
    %54 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_1720339, i32 0, i32 3
    %55 = load ptr, ptr %54
    %56 = call i32 %55(%List* %52)
    call void @println_i32(i32 %56)
    ret void
}
define i1 @lambda_1278852808(i8* %53) {
entry:
    %58 = alloca [6 x i8], align 1
    %59 = getelementptr inbounds [6 x i8], [6 x i8]* %58, i32 0, i32 0
    store i8 104, i8* %59
    %60 = getelementptr inbounds [6 x i8], [6 x i8]* %58, i32 0, i32 1
    store i8 101, i8* %60
    %61 = getelementptr inbounds [6 x i8], [6 x i8]* %58, i32 0, i32 2
    store i8 108, i8* %61
    %62 = getelementptr inbounds [6 x i8], [6 x i8]* %58, i32 0, i32 3
    store i8 108, i8* %62
    %63 = getelementptr inbounds [6 x i8], [6 x i8]* %58, i32 0, i32 4
    store i8 111, i8* %63
    %64 = getelementptr inbounds [6 x i8], [6 x i8]* %58, i32 0, i32 5
    store i8 0, i8* %64
    %65 = call i32 @strcmp(i8* %53, i8* %58)
    %66 = icmp eq i32 %65, 0
    ret i1 %66
}