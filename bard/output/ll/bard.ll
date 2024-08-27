declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
%Point = type { i32, i32, i1 }
define void @main() {
entry:
    %1 = call %Point* @new_point()
    call i1 @print_point(%Point* %1)
    call i1 @change_point(%Point* %1)
    call i1 @print_point(%Point* %1)
    ret void
}
define %Point* @new_point() {
entry:
    %8 = alloca %Point, align 8
    %9 = getelementptr inbounds %Point, %Point* %8, i32 0, i32 0
    store i32 100, i32* %9
    %10 = getelementptr inbounds %Point, %Point* %8, i32 0, i32 1
    store i32 400, i32* %10
    %11 = getelementptr inbounds %Point, %Point* %8, i32 0, i32 2
    store i1 1, i1* %11
    ret %Point* %8
}
define void @change_point(%Point* %12) {
entry:
    %13 = getelementptr inbounds %Point, %Point* %12, i32 0, i32 0
    store i32 200, i32* %13
    %14 = getelementptr inbounds %Point, %Point* %12, i32 0, i32 1
    store i32 800, i32* %14
    %15 = getelementptr inbounds %Point, %Point* %12, i32 0, i32 2
    store i1 0, i1* %15
    ret void
}
define void @print_point(%Point* %16) {
entry:
    %17 = getelementptr inbounds %Point, %Point* %16, i32 0, i32 0
    %18 = load i32, i32* %17
    call void @println_i32(i32 %18)
    %20 = getelementptr inbounds %Point, %Point* %16, i32 0, i32 1
    %21 = load i32, i32* %20
    call void @println_i32(i32 %21)
    %23 = getelementptr inbounds %Point, %Point* %16, i32 0, i32 2
    %24 = load i1, i1* %23
    call void @println_bool(i1 %24)
    ret void
}