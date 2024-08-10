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
declare i32 @printf(i8*, ...)
%0 = type { i32, i32, i32 }
define void @main() {
entry:
%2 = alloca %0, align 1
%4 = add i32 69, 0
%6 = getelementptr inbounds %0, %0* %2, i32 0, i32 0
store i32 %4, %0* %6
%8 = add i32 50, 0
%10 = getelementptr inbounds %0, %0* %2, i32 0, i32 1
store i32 %8, %0* %10
%12 = add i32 40, 0
%14 = getelementptr inbounds %0, %0* %2, i32 0, i32 2
store i32 %12, %0* %14
%16 = getelementptr inbounds %0, %0* %2, i32 0, i32 0
%18 = load i32, i32* %16
call void @println_i32(i32 %18)
%20 = getelementptr inbounds %0, %0* %2, i32 0, i32 1
%22 = load i32, i32* %20
call void @println_i32(i32 %22)
%24 = getelementptr inbounds %0, %0* %2, i32 0, i32 2
%26 = load i32, i32* %24
call void @println_i32(i32 %26)
ret void
}
define i8* @test() {
entry:
%28 = alloca [14 x i8], align 1
%30 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 0
store i8 72, i8* %30
%32 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 1
store i8 101, i8* %32
%34 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 2
store i8 108, i8* %34
%36 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 3
store i8 108, i8* %36
%38 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 4
store i8 111, i8* %38
%40 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 5
store i8 44, i8* %40
%42 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 6
store i8 32, i8* %42
%44 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 7
store i8 87, i8* %44
%46 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 8
store i8 111, i8* %46
%48 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 9
store i8 114, i8* %48
%50 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 10
store i8 108, i8* %50
%52 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 11
store i8 100, i8* %52
%54 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 12
store i8 33, i8* %54
%56 = getelementptr inbounds [14 x i8], [14 x i8]* %28, i32 0, i32 13
store i8 0, i8* %56
%58 = call i8* @malloc(i32 64)
call void @memset(i8* %58, i32 0, i32 64)
call i8* @strcat(i8* %58, i8* %30)
ret i8* %58
}