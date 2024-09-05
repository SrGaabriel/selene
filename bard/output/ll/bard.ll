declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
define void @callback(i32 %0, ptr %1) {
entry:
    %2 = call i32 %1(i32 %0)
    call void @println_i32(i32 %2)
    ret void
}
define void @main() {
entry:
    %4 = alloca [14 x i8], align 1
    %5 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 0
    store i8 72, i8* %5
    %6 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 1
    store i8 101, i8* %6
    %7 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 2
    store i8 108, i8* %7
    %8 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 3
    store i8 108, i8* %8
    %9 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 4
    store i8 111, i8* %9
    %10 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 5
    store i8 44, i8* %10
    %11 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 6
    store i8 32, i8* %11
    %12 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 7
    store i8 87, i8* %12
    %13 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 8
    store i8 111, i8* %13
    %14 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 9
    store i8 114, i8* %14
    %15 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 10
    store i8 108, i8* %15
    %16 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 11
    store i8 100, i8* %16
    %17 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 12
    store i8 33, i8* %17
    %18 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 13
    store i8 0, i8* %18
    %19 = getelementptr inbounds [14 x i8], [14 x i8]* %4, i32 0, i32 0
    call void @println_str(i8* %19)
    %21 = add i32 2, 0
    call i1 @callback(i32 %21, ptr @lambda_1487500813)
    %25 = add i32 4, 0
    call i1 @callback(i32 %25, ptr @lambda_1886491834)
    %29 = add i32 8, 0
    call i1 @callback(i32 %29, ptr @lambda_1536471117)
    ret void
}
define i32 @lambda_1487500813(i32 %22) {
entry:
    %33 = add i32 %22, 12
    ret i32 %33
}
define i32 @lambda_1886491834(i32 %26) {
entry:
    %34 = add i32 %26, 24
    ret i32 %34
}
define i32 @lambda_1536471117(i32 %30) {
entry:
    %35 = add i32 %30, 36
    ret i32 %35
}