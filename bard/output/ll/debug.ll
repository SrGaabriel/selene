@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
@format_i = private unnamed_addr constant [3 x i8] c"%d\00"

declare i32 @printf(i8*, ...)
declare void @println_str(i8*)
declare void @println_i32(i32)
declare i8* @malloc(i32)
declare void @memset(i8*, i32, i32)
declare i8* @strcat(i8*, i8*)

%Point = type { i32, i32 }

@msg_start_main = private unnamed_addr constant [14 x i8] c"Starting main\00"
@msg_point_init = private unnamed_addr constant [18 x i8] c"Point initialized\00"
@msg_fn_ptr_loaded = private unnamed_addr constant [24 x i8] c"Function pointer loaded\00"
@msg_fn_ptr_value = private unnamed_addr constant [19 x i8] c"Function pointer: \00"
@msg_calling_fn = private unnamed_addr constant [17 x i8] c"Calling function\00"
@msg_fn_returned = private unnamed_addr constant [23 x i8] c"Function call returned\00"
@msg_point_area_called = private unnamed_addr constant [18 x i8] c"Point_area called\00"
@msg_point_area_ret = private unnamed_addr constant [21 x i8] c"Point_area returning\00"
@msg_point_perimeter_called = private unnamed_addr constant [23 x i8] c"Point_perimeter called\00"
@msg_point_perimeter_ret = private unnamed_addr constant [26 x i8] c"Point_perimeter returning\00"

define void @main() {
entry:
    ; Debug: Starting main
    call void @println_str(i8* getelementptr([14 x i8], [14 x i8]* @msg_start_main, i32 0, i32 0))
    
    ; Allocate memory for Point
    %4 = call i8* @malloc(i32 8)
    call void @memset(i8* %4, i32 0, i32 8)
    %6 = bitcast i8* %4 to %Point*
    
    ; Initialize Point coordinates
    %8 = add i32 0, 0
    %10 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 0
    store i32 %8, i32* %10
    %12 = add i32 0, 0
    %14 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 1
    store i32 %12, i32* %14

    ; Debug: Point initialized
    call void @println_str(i8* getelementptr([18 x i8], [18 x i8]* @msg_point_init, i32 0, i32 0))
    
    ; Load function pointer for Point_area from trait_2
    %16 = getelementptr inbounds {i16, i16, ptr, ptr}, ptr @trait_2, i32 0, i32 2
    %18 = load ptr, ptr %16
    
    ; Debug: Loaded function pointer
    call void @println_str(i8* getelementptr([24 x i8], [24 x i8]* @msg_fn_ptr_loaded, i32 0, i32 0))
    
    ; Convert function pointer to integer and print
    %19 = ptrtoint ptr %18 to i32
    %msg_fn_ptr_val = getelementptr [24 x i8], [24 x i8]* @msg_fn_ptr_value, i32 0, i32 0
    call void @printf(i8* %msg_fn_ptr_val, i32 %19)

    ; Debug: Calling function
    call void @println_str(i8* getelementptr([17 x i8], [17 x i8]* @msg_calling_fn, i32 0, i32 0))
    
    ; Call the function through the function pointer
    %20 = call i32 %18(%Point* %6)
    
    ; Debug: Function call returned
    call void @println_str(i8* getelementptr([23 x i8], [23 x i8]* @msg_fn_returned, i32 0, i32 0))
    call void @println_i32(i32 %20)
    
    ret void
}

define i32 @Point_area(%Point* %22) {
entry:
    ; Debug: Point_area called
    call void @println_str(i8* getelementptr([18 x i8], [18 x i8]* @msg_point_area_called, i32 0, i32 0))
    
    %24 = alloca [14 x i8], align 1
    %26 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 0
    store i8 65, i8* %26
    %28 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 1
    store i8 114, i8* %28
    %30 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 2
    store i8 101, i8* %30
    %32 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 3
    store i8 97, i8* %32
    %34 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 4
    store i8 32, i8* %34
    %36 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 5
    store i8 111, i8* %36
    %38 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 6
    store i8 102, i8* %38
    %40 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 7
    store i8 32, i8* %40
    %42 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 8
    store i8 80, i8* %42
    %44 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 9
    store i8 111, i8* %44
    %46 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 10
    store i8 105, i8* %46
    %48 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 11
    store i8 110, i8* %48
    %50 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 12
    store i8 116, i8* %50
    %52 = getelementptr inbounds [14 x i8], [14 x i8]* %24, i32 0, i32 13
    store i8 0, i8* %52
    
    ; Allocate and set up string for output
    %54 = call i8* @malloc(i32 64)
    call void @memset(i8* %54, i32 0, i32 64)
    call i8* @strcat(i8* %54, i8* %26)
    
    ; Print string
    call void @println_str(i8* %54)
    
    %58 = add i32 0, 0
    
    ; Debug: Point_area returning
    call void @println_str(i8* getelementptr([21 x i8], [21 x i8]* @msg_point_area_ret, i32 0, i32 0))
    
    ret i32 %58
}

define i32 @Point_perimeter(%Point* %60) {
entry:
    ; Debug: Point_perimeter called
    call void @println_str(i8* getelementptr([23 x i8], [23 x i8]* @msg_point_perimeter_called, i32 0, i32 0))
    
    %62 = add i32 0, 0
    
    ; Debug: Point_perimeter returning
    call void @println_str(i8* getelementptr([26 x i8], [26 x i8]* @msg_point_perimeter_ret, i32 0, i32 0))
    
    ret i32 %62
}

@trait_2 = private unnamed_addr constant <{ i16, i16, ptr, ptr }> <{
    i16 8,
    i16 8,
    ptr @Point_area, 
    ptr @Point_perimeter
}>, align 8
