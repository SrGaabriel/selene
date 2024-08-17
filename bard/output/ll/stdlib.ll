            @trait_-203836479 = unnamed_addr constant <{ i16, i16, ptr, ptr }> <{
                i16 8,
                i16 8,
                ptr @Point_area, 
ptr @Point_perimeter
            }>, align 8
@trait_-1363293935 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_length
}>, align 8
@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"

            define i32 @str_length(i8* %str) {
            entry:
                %len = alloca i32
                store i32 0, i32* %len
                br label %loop
            
            loop:
                %idx = load i32, i32* %len
                %char_ptr = getelementptr inbounds i8, i8* %str, i32 %idx
                %char = load i8, i8* %char_ptr
                %is_null = icmp eq i8 %char, 0
                br i1 %is_null, label %end, label %next
            
            next:
                %next_idx = add i32 %idx, 1
                store i32 %next_idx, i32* %len
                br label %loop
            
            end:
                %final_len = load i32, i32* %len
                ret i32 %final_len
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

declare i32 @getchar()
@buffer = global [256 x i8] zeroinitializer
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
declare i32 @putchar(i32)
declare i32 @printf(i8*, ...)
define i32 @string_length(i8** %0) {
entry:
%2 = call i32 @str_length(i8** %0)
ret i32 %2
}
%Point = type { i32, i32 }
define i32 @Point_area(%Point* %6) {
entry:
%8 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 0
%10 = load i32, i32* %8
%12 = getelementptr inbounds %Point, %Point* %6, i32 0, i32 1
%14 = load i32, i32* %12
%16 = mul i32 %10, %14
ret i32 %16
}
define i32 @Point_perimeter(%Point* %18) {
entry:
%20 = getelementptr inbounds %Point, %Point* %18, i32 0, i32 0
%22 = load i32, i32* %20
%24 = getelementptr inbounds %Point, %Point* %18, i32 0, i32 1
%26 = load i32, i32* %24
%28 = add i32 %22, %26
%30 = mul i32 2, %28
ret i32 %30
}