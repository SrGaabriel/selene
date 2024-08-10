@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i8* @malloc(i32)
declare void @memset(i8*, i32, i32)
declare i8* @strcat(i8*, i8*)
declare i32 @getchar()
@buffer = global [256 x i8] zeroinitializer
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

declare i32 @putchar(i32)
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
    call i32 @putchar(i32 10)
    ret void
}

define void @println_bool(i1 %bool) {
entry:
    %format = getelementptr [3 x i8], [3 x i8]* @format_b, i32 0, i32 0
    %num = zext i1 %bool to i32
    call i32 (i8*, ...) @printf(i8* %format, i32 %num)
    call i32 @putchar(i32 10)
    ret void
}
declare i32 @printf(i8*, ...)
%Point = type { i32, i1, i32 }
define void @main() {
entry:
%2 = call %Point* @new_point()
call i1 @print_point(%Point* %2)
ret void
}
define %Point** @new_point() {
entry:
%4 = call i8* @malloc(i32 9)
call void @memset(i8* %4, i32 0, i32 9)
%6 = bitcast i8* %4 to %Point*
%8 = add i32 50, 0
%10 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 0
store i32 %8, i32* %10
%12 = add i1 1, 0
%14 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 1
store i1 %12, i1* %14
%16 = add i32 25, 0
%18 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 2
store i32 %16, i32* %18
%20 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 0
%22 = load i32, i32* %20
call void @println_i32(i32 %22)
%24 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 1
%26 = load i1, i1* %24
call void @println_bool(i1 %26)
%28 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 2
%30 = load i32, i32* %28
call void @println_i32(i32 %30)
ret %Point* %6
}
define void @print_point(%Point* %32) {
entry:
%34 = getelementptr inbounds %Point, %Point* %32, i32 0, i32 0
%36 = load i32, i32* %34
call void @println_i32(i32 %36)
%38 = getelementptr inbounds %Point, %Point* %32, i32 0, i32 1
%40 = load i1, i1* %38
call void @println_bool(i1 %40)
%42 = getelementptr inbounds %Point, %Point* %32, i32 0, i32 2
%44 = load i32, i32* %42
call void @println_i32(i32 %44)
ret void
}
define i8** @test() {
entry:
%46 = alloca [14 x i8], align 1
%48 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 0
store i8 72, i8* %48
%50 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 1
store i8 101, i8* %50
%52 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 2
store i8 108, i8* %52
%54 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 3
store i8 108, i8* %54
%56 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 4
store i8 111, i8* %56
%58 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 5
store i8 44, i8* %58
%60 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 6
store i8 32, i8* %60
%62 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 7
store i8 87, i8* %62
%64 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 8
store i8 111, i8* %64
%66 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 9
store i8 114, i8* %66
%68 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 10
store i8 108, i8* %68
%70 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 11
store i8 100, i8* %70
%72 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 12
store i8 33, i8* %72
%74 = getelementptr inbounds [14 x i8], [14 x i8]* %46, i32 0, i32 13
store i8 0, i8* %74
%76 = call i8* @malloc(i32 64)
call void @memset(i8* %76, i32 0, i32 64)
call i8* @strcat(i8* %76, i8* %48)
ret i8* %76
}