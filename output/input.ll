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
define void @main(i32 %0, i8** %2) {
entry:
%4 = alloca [6 x i8], align 1
%6 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 0
store i8 97, i8* %6
%8 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 1
store i8 112, i8* %8
%10 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 2
store i8 112, i8* %10
%12 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 3
store i8 108, i8* %12
%14 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 4
store i8 101, i8* %14
%16 = getelementptr inbounds [6 x i8], [6 x i8]* %4, i32 0, i32 5
store i8 0, i8* %16
%18 = call i8* @malloc(i32 64)
call void @memset(i8* %18, i32 0, i32 64)
call i8* @strcat(i8* %18, i8* %6)
%22 = alloca [7 x i8], align 1
%24 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 0
store i8 98, i8* %24
%26 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 1
store i8 97, i8* %26
%28 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 2
store i8 110, i8* %28
%30 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 3
store i8 97, i8* %30
%32 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 4
store i8 110, i8* %32
%34 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 5
store i8 97, i8* %34
%36 = getelementptr inbounds [7 x i8], [7 x i8]* %22, i32 0, i32 6
store i8 0, i8* %36
%38 = call i8* @malloc(i32 64)
call void @memset(i8* %38, i32 0, i32 64)
call i8* @strcat(i8* %38, i8* %24)
%42 = alloca [7 x i8], align 1
%44 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 0
store i8 99, i8* %44
%46 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 1
store i8 104, i8* %46
%48 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 2
store i8 101, i8* %48
%50 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 3
store i8 114, i8* %50
%52 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 4
store i8 114, i8* %52
%54 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 5
store i8 121, i8* %54
%56 = getelementptr inbounds [7 x i8], [7 x i8]* %42, i32 0, i32 6
store i8 0, i8* %56
%58 = call i8* @malloc(i32 64)
call void @memset(i8* %58, i32 0, i32 64)
call i8* @strcat(i8* %58, i8* %44)
%62 = call i8* @malloc(i32 8)
%64 = getelementptr inbounds i8*, i8** %62, i32 0
store i8* %18, i8** %64
%66 = getelementptr inbounds i8*, i8** %62, i32 1
store i8* %38, i8** %66
%68 = getelementptr inbounds i8*, i8** %62, i32 2
store i8* %58, i8** %68
%70 = add i32 3, 0
%72 = getelementptr inbounds i8*, i8** %64, i32 %70
%74 = load i8*, i8** %72
call void @println_str(i8* %74)
ret void
}
define i8* @test() {
entry:
%76 = alloca [14 x i8], align 1
%78 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 0
store i8 72, i8* %78
%80 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 1
store i8 101, i8* %80
%82 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 2
store i8 108, i8* %82
%84 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 3
store i8 108, i8* %84
%86 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 4
store i8 111, i8* %86
%88 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 5
store i8 44, i8* %88
%90 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 6
store i8 32, i8* %90
%92 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 7
store i8 87, i8* %92
%94 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 8
store i8 111, i8* %94
%96 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 9
store i8 114, i8* %96
%98 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 10
store i8 108, i8* %98
%100 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 11
store i8 100, i8* %100
%102 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 12
store i8 33, i8* %102
%104 = getelementptr inbounds [14 x i8], [14 x i8]* %76, i32 0, i32 13
store i8 0, i8* %104
%106 = call i8* @malloc(i32 64)
call void @memset(i8* %106, i32 0, i32 64)
call i8* @strcat(i8* %106, i8* %78)
ret i8* %106
}