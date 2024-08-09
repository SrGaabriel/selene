@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i8* @malloc(i32)
declare i8* @strcpy(i8*, i8*)
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
define void @main() {
entry:
%0 = alloca [14 x i8], align 1
%1 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 0
store i8 72, i8* %1
%2 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 1
store i8 101, i8* %2
%3 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 2
store i8 108, i8* %3
%4 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 3
store i8 108, i8* %4
%5 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 4
store i8 111, i8* %5
%6 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 5
store i8 44, i8* %6
%7 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 6
store i8 32, i8* %7
%8 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 7
store i8 87, i8* %8
%9 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 8
store i8 111, i8* %9
%10 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 9
store i8 114, i8* %10
%11 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 10
store i8 108, i8* %11
%12 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 11
store i8 100, i8* %12
%13 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 12
store i8 33, i8* %13
%14 = getelementptr inbounds [14 x i8], [14 x i8]* %0, i32 0, i32 13
store i8 0, i8* %14
%15 = alloca [4 x i8], align 1
%16 = getelementptr inbounds [4 x i8], [4 x i8]* %15, i32 0, i32 0
store i8 33, i8* %16
%17 = getelementptr inbounds [4 x i8], [4 x i8]* %15, i32 0, i32 1
store i8 33, i8* %17
%18 = getelementptr inbounds [4 x i8], [4 x i8]* %15, i32 0, i32 2
store i8 64, i8* %18
%19 = getelementptr inbounds [4 x i8], [4 x i8]* %15, i32 0, i32 3
store i8 0, i8* %19
%20 = call i8* @malloc(i32 64)
call i8* @strcpy(i8* %20, i8* %1)
call i8* @strcat(i8* %20, i8* %16)
call void @println_str(i8* %20)
ret void
}