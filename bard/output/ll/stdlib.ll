declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
@trait_640113647 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @int32.text
}>, align 8
@trait_530042637 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string.text
}>, align 8
@trait_1288235781 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string.length
}>, align 8
            @trait_758119607 = unnamed_addr constant <{ i16, i16, ptr, ptr, ptr, ptr, ptr }> <{
                i16 8,
                i16 8,
                ptr @List.new, 
ptr @List.size, 
ptr @List.get, 
ptr @List.filter, 
ptr @List.push
            }>, align 8
%TcpServer = type { i32, i16 }
%Socket = type { i32, i32, i16, i32 }
%List = type { i8**, i32 }
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
define %List* @List.new() {
entry:
    %1 = call i8* @malloc(i32 64)
    call void @memset(i8* %1, i32 0, i32 64)
    %2 = bitcast i8* %1 to i8***
    %3 = alloca %List, align 8
    %4 = getelementptr inbounds %List, %List* %3, i32 0, i32 0
    store i8*** %2, i8**** %4
    %5 = getelementptr inbounds %List, %List* %3, i32 0, i32 1
    store i32 0, i32* %5
    ret %List* %3
}
define i32 @List.size(%List* %6) {
entry:
    %7 = getelementptr inbounds %List, %List* %6, i32 0, i32 1
    %8 = load i32, i32* %7
    ret i32 %8
}
define i8* @List.get(%List* %9, i32 %10) {
entry:
    %11 = getelementptr inbounds %List, %List* %9, i32 0, i32 0
    %12 = load i8**, i8*** %11
    %13 = getelementptr inbounds i8*, i8** %12, i32 %10
    %14 = load i8*, i8** %13
    ret i8* %14
}
define %List* @List.filter(%List* %15, ptr %16) {
entry:
    %17 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_758119607, i32 0, i32 2
    %18 = load ptr, ptr %17
    %19 = call %List* %18()
    %20 = alloca i32, align 4
    store i32 0, i32* %20
    %21 = getelementptr inbounds %List, %List* %15, i32 0, i32 1
    %22 = load i32, i32* %21
    br label %for_condition0
for_condition0:
    %24 = load i32, i32* %20
    %25 = icmp sle i32 %24, %22
    br i1 %25, label %for_body1, label %for_end2
for_body1:
    %26 = load i32, i32* %20
    %27 = getelementptr inbounds %List, %List* %15, i32 0, i32 0
    %28 = load i8**, i8*** %27
    %29 = getelementptr inbounds i8*, i8** %28, i32 %26
    %30 = load i8*, i8** %29
    %31 = call i1 %16(i8* %30)
    br i1 %31, label %label3, label %label5
label3:
    %32 = getelementptr inbounds <{i16, i16, ptr, ptr, ptr, ptr, ptr}>, ptr @trait_758119607, i32 0, i32 6
    %33 = load ptr, ptr %32
    %34 = getelementptr inbounds %List, %List* %15, i32 0, i32 0
    %35 = load i8**, i8*** %34
    %36 = getelementptr inbounds i8*, i8** %35, i32 %26
    %37 = load i8*, i8** %36
    call i1 %33(%List* %19, i8* %37)
    br label %label5
label5:
    %40 = add i32 %24, 1
    store i32 %40, i32* %20
    br label %for_condition0
for_end2:
    ret %List* %19
}
define void @List.push(%List* %42, i8* %43) {
entry:
    %44 = getelementptr inbounds %List, %List* %42, i32 0, i32 0
    %45 = load i8**, i8*** %44
    %46 = getelementptr inbounds %List, %List* %42, i32 0, i32 1
    %47 = load i32, i32* %46
    %48 = getelementptr inbounds i8*, i8** %45, i32 %47
    store i8* %43, i8** %48
    %49 = getelementptr inbounds %List, %List* %42, i32 0, i32 1
    %50 = load i32, i32* %49
    %51 = add i32 %50, 1
    %52 = getelementptr inbounds %List, %List* %42, i32 0, i32 1
    store i32 %51, i32* %52
    ret void
}
define i32 @string.length(i8** %55) {
entry:
    %56 = call i32 @str_length(i8** %55)
    ret i32 %56
}
define i8* @string.text(i8** %57) {
entry:
    ret i8** %57
}
define i8* @int32.text(i32* %58) {
entry:
    %59 = alloca [6 x i8], align 1
    %60 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 0
    store i8 105, i8* %60
    %61 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 1
    store i8 110, i8* %61
    %62 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 2
    store i8 116, i8* %62
    %63 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 3
    store i8 51, i8* %63
    %64 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 4
    store i8 50, i8* %64
    %65 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 5
    store i8 0, i8* %65
    %66 = getelementptr inbounds [6 x i8], [6 x i8]* %59, i32 0, i32 0
    ret i8* %66
}