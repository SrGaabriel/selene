            @trait_1625244645 = unnamed_addr constant <{ i16, i16, ptr, ptr }> <{
                i16 8,
                i16 8,
                ptr @Map_new, 
ptr @Map_fooat
            }>, align 8
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
%Map = type { i32, i32 }
define %Map* @Map_new(i32 %1) {
entry:
    %2 = alloca %Map, align 8
    %3 = getelementptr inbounds %Map, %Map* %2, i32 0, i32 0
    store i32 %1, i32* %3
    %4 = getelementptr inbounds %Map, %Map* %2, i32 0, i32 1
    store i32 42, i32* %4
    ret %Map* %2
}
define i32 @Map_fooat(%Map* %5) {
entry:
    %6 = getelementptr inbounds %Map, %Map* %5, i32 0, i32 0
    %7 = load i32, i32* %6
    ret i32 %7
}
define void @main() {
entry:
    %8 = alloca [14 x i8], align 1
    %9 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 0
    store i8 72, i8* %9
    %10 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 1
    store i8 101, i8* %10
    %11 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 2
    store i8 108, i8* %11
    %12 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 3
    store i8 108, i8* %12
    %13 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 4
    store i8 111, i8* %13
    %14 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 5
    store i8 44, i8* %14
    %15 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 6
    store i8 32, i8* %15
    %16 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 7
    store i8 87, i8* %16
    %17 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 8
    store i8 111, i8* %17
    %18 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 9
    store i8 114, i8* %18
    %19 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 10
    store i8 108, i8* %19
    %20 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 11
    store i8 100, i8* %20
    %21 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 12
    store i8 33, i8* %21
    %22 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 13
    store i8 0, i8* %22
    %23 = getelementptr inbounds [14 x i8], [14 x i8]* %8, i32 0, i32 0
    call void @println_str(i8* %23)
    %25 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1625244645, i32 0, i32 2
    %26 = load ptr, ptr %25
    %27 = call %Map* %26(i32 4)
    %28 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1625244645, i32 0, i32 3
    %29 = load ptr, ptr %28
    %30 = call i32 %29(%Map* %27)
    call void @println_i32(i32 %30)
    %32 = alloca %Map, align 8
    %33 = getelementptr inbounds %Map, %Map* %32, i32 0, i32 0
    store i32 42, i32* %33
    %34 = getelementptr inbounds %Map, %Map* %32, i32 0, i32 1
    store i32 64, i32* %34
    %35 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1625244645, i32 0, i32 3
    %36 = load ptr, ptr %35
    %37 = call i32 %36(%Map* %32)
    call void @println_i32(i32 %37)
    %39 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1625244645, i32 0, i32 2
    %40 = load ptr, ptr %39
    %41 = call %Map* %40(i32 8)
    %42 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1625244645, i32 0, i32 3
    %43 = load ptr, ptr %42
    %44 = call i32 %43(%Map* %41)
    call void @println_i32(i32 %44)
    ret void
}