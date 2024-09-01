declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
define void @callback(ptr %0) {
entry:
    %1 = call i32 %0(i32 42)
    call void @println_i32(i32 %1)
    ret void
}
define void @main() {
entry:
    %3 = alloca [14 x i8], align 1
    %4 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 0
    store i8 72, i8* %4
    %5 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 1
    store i8 101, i8* %5
    %6 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 2
    store i8 108, i8* %6
    %7 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 3
    store i8 108, i8* %7
    %8 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 4
    store i8 111, i8* %8
    %9 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 5
    store i8 44, i8* %9
    %10 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 6
    store i8 32, i8* %10
    %11 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 7
    store i8 87, i8* %11
    %12 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 8
    store i8 111, i8* %12
    %13 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 9
    store i8 114, i8* %13
    %14 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 10
    store i8 108, i8* %14
    %15 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 11
    store i8 100, i8* %15
    %16 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 12
    store i8 33, i8* %16
    %17 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 13
    store i8 0, i8* %17
    %18 = getelementptr inbounds [14 x i8], [14 x i8]* %3, i32 0, i32 0
    call void @println_str(i8* %18)
    call i1 @callback(ptr @lambda_1276611190)
    call i1 @callback(ptr @lambda_1583159071)
    call i1 @callback(ptr @lambda_2079179914)
    ret void
}
define i32 @lambda_1276611190(i32 %20) {
entry:
    %29 = add i32 %20, 12
    ret i32 %29
}
define i32 @lambda_1583159071(i32 %23) {
entry:
    %30 = add i32 %23, 24
    ret i32 %30
}
define i32 @lambda_2079179914(i32 %26) {
entry:
    %31 = add i32 %26, 36
    ret i32 %31
}