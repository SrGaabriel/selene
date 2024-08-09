declare i32 @strcmp(i8*, i8*)
declare i32 @strlen(i8*)
declare i8* @malloc(i32)
declare void @memcpy(i8*, i8*, i32)
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@buffer = global [256 x i8] zeroinitializer
declare i32 @getchar()
declare i32 @printf(i8*, ...)
define void @println_str(i8* %str) {
entry:
    %format = alloca [4 x i8], align 1
    store [4 x i8] [i8 37, i8 115, i8 10, i8 0], [4 x i8]* %format, align 1

    %formatStr = getelementptr [4 x i8], [4 x i8]* %format, i32 0, i32 0

    call i32 @printf(i8* %formatStr, i8* %str)

    ret void
}

define void @println_i32(i32 %num) {
entry:
    %format = getelementptr [3 x i8], [3 x i8]* @format_n, i32 0, i32 0
    call i32 (i8*, ...) @printf(i8* %format, i32 %num)
    ret void
}
define i8* @readln() {
entry:
    %buffer_ptr = getelementptr inbounds [256 x i8], [256 x i8]* @buffer, i32 0, i32 0
    %i = alloca i32
    store i32 0, i32* %i
    br label %read_loop

read_loop:
    %idx = load i32, i32* %i
    %char_ptr = getelementptr inbounds i8, i8* %buffer_ptr, i32 %idx
    %char = call i32 @getchar()
    %char_i8 = trunc i32 %char to i8
    store i8 %char_i8, i8* %char_ptr
    %is_newline = icmp eq i32 %char, 10
    %next_idx = add i32 %idx, 1
    store i32 %next_idx, i32* %i
    br i1 %is_newline, label %end_read, label %read_loop

end_read:
    %last_idx = sub i32 %next_idx, 1
    %last_char_ptr = getelementptr inbounds i8, i8* %buffer_ptr, i32 %last_idx
    store i8 0, i8* %last_char_ptr
    ret i8* %buffer_ptr
}

define void @main() {
entry:
    call void @check_typed()
    ret void
}
define void @check_if_typed_apple(i8* %1) {
entry:
    %2 = alloca [1 x i8]
    %3 = getelementptr inbounds [1 x i8], [1 x i8]* %2, i32 0, i32 0
    store i8 97, i8* %3
    %4 = getelementptr inbounds [1 x i8], [1 x i8]* %2, i32 0, i32 1
    store i8 112, i8* %4
    %5 = getelementptr inbounds [1 x i8], [1 x i8]* %2, i32 0, i32 2
    store i8 112, i8* %5
    %6 = getelementptr inbounds [1 x i8], [1 x i8]* %2, i32 0, i32 3
    store i8 108, i8* %6
    %7 = getelementptr inbounds [1 x i8], [1 x i8]* %2, i32 0, i32 4
    store i8 101, i8* %7
    %8 = getelementptr inbounds [1 x i8], [1 x i8]* %2, i32 0, i32 5
    store i8 0, i8* %8
    %9 = alloca i8*
    store i8* %3, i8** %9
    %10 = call i1 @strcmp(i8* %1, i8* %3)
    %11 = icmp eq i1 %10, 0
    %cmp = icmp ne i1 %11, 0
    br i1 %cmp, label %then_0, label %else_1
then_0:
    %12 = alloca [1 x i8]
    %13 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 0
    store i8 89, i8* %13
    %14 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 1
    store i8 111, i8* %14
    %15 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 2
    store i8 117, i8* %15
    %16 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 3
    store i8 32, i8* %16
    %17 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 4
    store i8 116, i8* %17
    %18 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 5
    store i8 121, i8* %18
    %19 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 6
    store i8 112, i8* %19
    %20 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 7
    store i8 101, i8* %20
    %21 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 8
    store i8 100, i8* %21
    %22 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 9
    store i8 32, i8* %22
    %23 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 10
    store i8 39, i8* %23
    %24 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 11
    store i8 97, i8* %24
    %25 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 12
    store i8 112, i8* %25
    %26 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 13
    store i8 112, i8* %26
    %27 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 14
    store i8 108, i8* %27
    %28 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 15
    store i8 101, i8* %28
    %29 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 16
    store i8 39, i8* %29
    %30 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 17
    store i8 33, i8* %30
    %31 = getelementptr inbounds [1 x i8], [1 x i8]* %12, i32 0, i32 18
    store i8 0, i8* %31
    call void @println_str(i8* %13)
    br label %endif_2
else_1:
    %33 = alloca [1 x i8]
    %34 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 0
    store i8 89, i8* %34
    %35 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 1
    store i8 111, i8* %35
    %36 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 2
    store i8 117, i8* %36
    %37 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 3
    store i8 32, i8* %37
    %38 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 4
    store i8 100, i8* %38
    %39 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 5
    store i8 105, i8* %39
    %40 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 6
    store i8 100, i8* %40
    %41 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 7
    store i8 110, i8* %41
    %42 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 8
    store i8 39, i8* %42
    %43 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 9
    store i8 116, i8* %43
    %44 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 10
    store i8 32, i8* %44
    %45 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 11
    store i8 116, i8* %45
    %46 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 12
    store i8 121, i8* %46
    %47 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 13
    store i8 112, i8* %47
    %48 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 14
    store i8 101, i8* %48
    %49 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 15
    store i8 32, i8* %49
    %50 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 16
    store i8 39, i8* %50
    %51 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 17
    store i8 97, i8* %51
    %52 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 18
    store i8 112, i8* %52
    %53 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 19
    store i8 112, i8* %53
    %54 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 20
    store i8 108, i8* %54
    %55 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 21
    store i8 101, i8* %55
    %56 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 22
    store i8 39, i8* %56
    %57 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 23
    store i8 46, i8* %57
    %58 = getelementptr inbounds [1 x i8], [1 x i8]* %33, i32 0, i32 24
    store i8 0, i8* %58
    call void @println_str(i8* %34)
    call void @check_typed()
    br label %endif_2
endif_2:
    ret void
}
define void @check_typed() {
entry:
    %1 = alloca [1 x i8]
    %2 = getelementptr inbounds [1 x i8], [1 x i8]* %1, i32 0, i32 0
    store i8 0, i8* %2
    call void @println_str(i8* %2)
    %4 = alloca [1 x i8]
    %5 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 0
    store i8 84, i8* %5
    %6 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 1
    store i8 121, i8* %6
    %7 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 2
    store i8 112, i8* %7
    %8 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 3
    store i8 101, i8* %8
    %9 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 4
    store i8 32, i8* %9
    %10 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 5
    store i8 115, i8* %10
    %11 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 6
    store i8 111, i8* %11
    %12 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 7
    store i8 109, i8* %12
    %13 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 8
    store i8 101, i8* %13
    %14 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 9
    store i8 116, i8* %14
    %15 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 10
    store i8 104, i8* %15
    %16 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 11
    store i8 105, i8* %16
    %17 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 12
    store i8 110, i8* %17
    %18 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 13
    store i8 103, i8* %18
    %19 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 14
    store i8 58, i8* %19
    %20 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 15
    store i8 32, i8* %20
    %21 = getelementptr inbounds [1 x i8], [1 x i8]* %4, i32 0, i32 16
    store i8 0, i8* %21
    %22 = call i32 @printf(i8* %5)
    %23 = call i8* @readln()
    %24 = alloca i8*
    store i8* %23, i8** %24
    %26 = add i32 1, 0
    %27 = alloca [11 x i8]
    %28 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 0
    store i8 89, i8* %28
    %29 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 1
    store i8 111, i8* %29
    %30 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 2
    store i8 117, i8* %30
    %31 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 3
    store i8 32, i8* %31
    %32 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 4
    store i8 116, i8* %32
    %33 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 5
    store i8 121, i8* %33
    %34 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 6
    store i8 112, i8* %34
    %35 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 7
    store i8 101, i8* %35
    %36 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 8
    store i8 100, i8* %36
    %37 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 9
    store i8 32, i8* %37
    %38 = getelementptr inbounds [11 x i8], [11 x i8]* %27, i32 0, i32 10
    store i8 0, i8* %38
    %40 = add i32 %26, 10
    %42 = call i32 @strlen(i8* %23)
    %43 = add i32 %40, %42
    %45 = alloca [3 x i8]
    %46 = getelementptr inbounds [3 x i8], [3 x i8]* %45, i32 0, i32 0
    store i8 33, i8* %46
    %47 = getelementptr inbounds [3 x i8], [3 x i8]* %45, i32 0, i32 1
    store i8 33, i8* %47
    %48 = getelementptr inbounds [3 x i8], [3 x i8]* %45, i32 0, i32 2
    store i8 0, i8* %48
    %50 = add i32 %43, 2
    %52 = call i8* @malloc(i32 %50)
    %53 = add i32 0, 0
    %54 = getelementptr inbounds i8, i8* %52, i32 %53
    call void @memcpy(i8* %54, i8* %27, i32 10)
    %55 = add i32 %53, 10
    %56 = getelementptr inbounds i8, i8* %52, i32 %55
    call void @memcpy(i8* %56, i8* %23, i32 %42)
    %57 = add i32 %55, %42
    %58 = getelementptr inbounds i8, i8* %52, i32 %57
    call void @memcpy(i8* %58, i8* %45, i32 2)
    %59 = add i32 %57, 2
    %60 = getelementptr inbounds i8, i8* %52, i32 %59
    store i8 0, i8* %60
    call void @println_str(i8* %52)
    call void @check_if_typed_apple(i8* %23)
    ret void
}