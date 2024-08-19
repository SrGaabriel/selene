@trait_1844174184 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @int32_text
}>, align 8
@trait_570336746 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_text
}>, align 8
@trait_1752161064 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_length
}>, align 8
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
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

define void @println_f64(double %num) {
entry:
    %format = getelementptr [4 x i8], [4 x i8]* @format_f, i32 0, i32 0
    call i32 (i8*, ...) @printf(i8* %format, double %num)
    call i32 @putchar(i32 10)
    ret void
}

define void @println_bool(i1 %bool) {
entry:
    %format = getelementptr [3 x i8], [3 x i8]* @format_n, i32 0, i32 0
    %num = zext i1 %bool to i32
    call i32 (i8*, ...) @printf(i8* %format, i32 %num)
    call i32 @putchar(i32 10)
    ret void
}
declare i32 @putchar(i32)
declare i32 @printf(i8*, ...)
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
define i32 @string_length(i8** %0) {
entry:
%1 = call i32 @str_length(i8** %0)
ret i32 %1
}
define i8* @string_text(i8** %2) {
entry:
ret i8** %2
}
define i8* @int32_text(i32* %3) {
entry:
%4 = alloca [6 x i8], align 1
%5 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 0
store i8 105, i8* %5
%6 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 1
store i8 110, i8* %6
%7 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 2
store i8 116, i8* %7
%8 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 3
store i8 51, i8* %8
%9 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 4
store i8 50, i8* %9
%10 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 5
store i8 0, i8* %10
ret i8* %5
}