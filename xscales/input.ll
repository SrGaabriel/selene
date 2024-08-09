@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i8* @malloc(i32)
declare void @memset(i8*, i32, i32)
declare i8* @strcat(i8*, i8*)
declare i32 @strcmp(i8*, i8*)
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
call i1 @check_typed()
ret void
}
define void @check_if_typed_apple(i8* %0) {
entry:
%2 = alloca [6 x i8], align 1
%4 = getelementptr inbounds [6 x i8], [6 x i8]* %2, i32 0, i32 0
store i8 97, i8* %4
%6 = getelementptr inbounds [6 x i8], [6 x i8]* %2, i32 0, i32 1
store i8 112, i8* %6
%8 = getelementptr inbounds [6 x i8], [6 x i8]* %2, i32 0, i32 2
store i8 112, i8* %8
%10 = getelementptr inbounds [6 x i8], [6 x i8]* %2, i32 0, i32 3
store i8 108, i8* %10
%12 = getelementptr inbounds [6 x i8], [6 x i8]* %2, i32 0, i32 4
store i8 101, i8* %12
%14 = getelementptr inbounds [6 x i8], [6 x i8]* %2, i32 0, i32 5
store i8 0, i8* %14
%16 = call i8* @malloc(i32 64)
call void @memset(i8* %16, i32 0, i32 64)
call i8* @strcat(i8* %16, i8* %4)
%20 = call i32 @strcmp(i8* %16, i8* %0)
%22 = icmp eq i32 %20, 0
br i1 %22, label %label0, label %label1
label0:
%24 = alloca [18 x i8], align 1
%26 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 0
store i8 89, i8* %26
%28 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 1
store i8 111, i8* %28
%30 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 2
store i8 117, i8* %30
%32 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 3
store i8 32, i8* %32
%34 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 4
store i8 116, i8* %34
%36 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 5
store i8 121, i8* %36
%38 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 6
store i8 112, i8* %38
%40 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 7
store i8 101, i8* %40
%42 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 8
store i8 100, i8* %42
%44 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 9
store i8 32, i8* %44
%46 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 10
store i8 39, i8* %46
%48 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 11
store i8 97, i8* %48
%50 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 12
store i8 112, i8* %50
%52 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 13
store i8 112, i8* %52
%54 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 14
store i8 108, i8* %54
%56 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 15
store i8 101, i8* %56
%58 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 16
store i8 39, i8* %58
%60 = getelementptr inbounds [18 x i8], [18 x i8]* %24, i32 0, i32 17
store i8 0, i8* %60
%62 = call i8* @malloc(i32 64)
call void @memset(i8* %62, i32 0, i32 64)
call i8* @strcat(i8* %62, i8* %26)
call void @println_str(i8* %62)
br label %label2
label1:
%66 = alloca [25 x i8], align 1
%68 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 0
store i8 89, i8* %68
%70 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 1
store i8 111, i8* %70
%72 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 2
store i8 117, i8* %72
%74 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 3
store i8 32, i8* %74
%76 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 4
store i8 100, i8* %76
%78 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 5
store i8 105, i8* %78
%80 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 6
store i8 100, i8* %80
%82 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 7
store i8 110, i8* %82
%84 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 8
store i8 39, i8* %84
%86 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 9
store i8 116, i8* %86
%88 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 10
store i8 32, i8* %88
%90 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 11
store i8 116, i8* %90
%92 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 12
store i8 121, i8* %92
%94 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 13
store i8 112, i8* %94
%96 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 14
store i8 101, i8* %96
%98 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 15
store i8 32, i8* %98
%100 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 16
store i8 39, i8* %100
%102 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 17
store i8 97, i8* %102
%104 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 18
store i8 112, i8* %104
%106 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 19
store i8 112, i8* %106
%108 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 20
store i8 108, i8* %108
%110 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 21
store i8 101, i8* %110
%112 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 22
store i8 39, i8* %112
%114 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 23
store i8 46, i8* %114
%116 = getelementptr inbounds [25 x i8], [25 x i8]* %66, i32 0, i32 24
store i8 0, i8* %116
%118 = call i8* @malloc(i32 64)
call void @memset(i8* %118, i32 0, i32 64)
call i8* @strcat(i8* %118, i8* %68)
call void @println_str(i8* %118)
call i1 @check_typed()
br label %label2
label2:
ret void
}
define void @check_typed() {
entry:
%122 = alloca [5 x i8], align 1
%124 = getelementptr inbounds [5 x i8], [5 x i8]* %122, i32 0, i32 0
store i8 45, i8* %124
%126 = getelementptr inbounds [5 x i8], [5 x i8]* %122, i32 0, i32 1
store i8 45, i8* %126
%128 = getelementptr inbounds [5 x i8], [5 x i8]* %122, i32 0, i32 2
store i8 45, i8* %128
%130 = getelementptr inbounds [5 x i8], [5 x i8]* %122, i32 0, i32 3
store i8 45, i8* %130
%132 = getelementptr inbounds [5 x i8], [5 x i8]* %122, i32 0, i32 4
store i8 0, i8* %132
%134 = call i8* @malloc(i32 64)
call void @memset(i8* %134, i32 0, i32 64)
call i8* @strcat(i8* %134, i8* %124)
call void @println_str(i8* %134)
%138 = alloca [17 x i8], align 1
%140 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 0
store i8 84, i8* %140
%142 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 1
store i8 121, i8* %142
%144 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 2
store i8 112, i8* %144
%146 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 3
store i8 101, i8* %146
%148 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 4
store i8 32, i8* %148
%150 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 5
store i8 115, i8* %150
%152 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 6
store i8 111, i8* %152
%154 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 7
store i8 109, i8* %154
%156 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 8
store i8 101, i8* %156
%158 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 9
store i8 116, i8* %158
%160 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 10
store i8 104, i8* %160
%162 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 11
store i8 105, i8* %162
%164 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 12
store i8 110, i8* %164
%166 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 13
store i8 103, i8* %166
%168 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 14
store i8 58, i8* %168
%170 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 15
store i8 32, i8* %170
%172 = getelementptr inbounds [17 x i8], [17 x i8]* %138, i32 0, i32 16
store i8 0, i8* %172
%174 = call i8* @malloc(i32 64)
call void @memset(i8* %174, i32 0, i32 64)
call i8* @strcat(i8* %174, i8* %140)
call i32 @printf(i8* %174)
%178 = call i8* @readln()
%180 = alloca [12 x i8], align 1
%182 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 0
store i8 89, i8* %182
%184 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 1
store i8 111, i8* %184
%186 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 2
store i8 117, i8* %186
%188 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 3
store i8 32, i8* %188
%190 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 4
store i8 116, i8* %190
%192 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 5
store i8 121, i8* %192
%194 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 6
store i8 112, i8* %194
%196 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 7
store i8 101, i8* %196
%198 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 8
store i8 100, i8* %198
%200 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 9
store i8 32, i8* %200
%202 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 10
store i8 96, i8* %202
%204 = getelementptr inbounds [12 x i8], [12 x i8]* %180, i32 0, i32 11
store i8 0, i8* %204
%206 = alloca [2 x i8], align 1
%208 = getelementptr inbounds [2 x i8], [2 x i8]* %206, i32 0, i32 0
store i8 96, i8* %208
%210 = getelementptr inbounds [2 x i8], [2 x i8]* %206, i32 0, i32 1
store i8 0, i8* %210
%212 = call i8* @malloc(i32 64)
call void @memset(i8* %212, i32 0, i32 64)
call i8* @strcat(i8* %212, i8* %182)
call i8* @strcat(i8* %212, i8* %178)
call i8* @strcat(i8* %212, i8* %208)
call void @println_str(i8* %212)
call i1 @check_if_typed_apple(i8* %178)
ret void
}