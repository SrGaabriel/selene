@trait_-1450239945 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_fooat
}>, align 8
@trait_-1736833064 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
@trait_-1363293935 = external constant <{ i16, i16, ptr }>
@trait_-203836479 = external constant <{ i16, i16, ptr, ptr }>
@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i32 @str_length(i8*)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
%Point = type { i32, i32 }
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
%Map = type { i32 }
define i32 @Map_fooat(%Map* %2) {
entry:
%4 = getelementptr inbounds %Map, %Map* %2, i32 0, i32 0
%6 = load i32, i32* %4
ret i32 %6
}
define i32 @string_fooat(i8** %8) {
entry:
%10 = alloca [9 x i8], align 1
%12 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 0
store i8 45, i8* %12
%14 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 1
store i8 61, i8* %14
%16 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 2
store i8 45, i8* %16
%18 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 3
store i8 61, i8* %18
%20 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 4
store i8 45, i8* %20
%22 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 5
store i8 61, i8* %22
%24 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 6
store i8 45, i8* %24
%26 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 7
store i8 61, i8* %26
%28 = getelementptr inbounds [9 x i8], [9 x i8]* %10, i32 0, i32 8
store i8 0, i8* %28
call void @println_str(i8* %12)
call void @println_str(i8** %8)
%30 = alloca [9 x i8], align 1
%32 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 0
store i8 45, i8* %32
%34 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 1
store i8 61, i8* %34
%36 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 2
store i8 45, i8* %36
%38 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 3
store i8 61, i8* %38
%40 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 4
store i8 45, i8* %40
%42 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 5
store i8 61, i8* %42
%44 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 6
store i8 45, i8* %44
%46 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 7
store i8 61, i8* %46
%48 = getelementptr inbounds [9 x i8], [9 x i8]* %30, i32 0, i32 8
store i8 0, i8* %48
call void @println_str(i8* %32)
%50 = add i32 42, 0
ret i32 %50
}
define void @main() {
entry:
%52 = alloca [14 x i8], align 1
%54 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 0
store i8 72, i8* %54
%56 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 1
store i8 101, i8* %56
%58 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 2
store i8 108, i8* %58
%60 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 3
store i8 108, i8* %60
%62 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 4
store i8 111, i8* %62
%64 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 5
store i8 44, i8* %64
%66 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 6
store i8 32, i8* %66
%68 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 7
store i8 87, i8* %68
%70 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 8
store i8 111, i8* %70
%72 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 9
store i8 114, i8* %72
%74 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 10
store i8 108, i8* %74
%76 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 11
store i8 100, i8* %76
%78 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 12
store i8 33, i8* %78
%80 = getelementptr inbounds [14 x i8], [14 x i8]* %52, i32 0, i32 13
store i8 0, i8* %80
call void @println_str(i8* %54)
%82 = call i8* @malloc(i32 8)
call void @memset(i8* %82, i32 0, i32 8)
%84 = bitcast i8* %82 to %Point*
%86 = getelementptr inbounds %Point, %Point* %84, i32 0, i32 0
store i32 24, i32* %86
%88 = getelementptr inbounds %Point, %Point* %84, i32 0, i32 1
store i32 4, i32* %88
%90 = getelementptr inbounds %Point, %Point* %84, i32 0, i32 0
%92 = load i32, i32* %90
call void @println_i32(i32 %92)
%94 = getelementptr inbounds %Point, %Point* %84, i32 0, i32 1
%96 = load i32, i32* %94
call void @println_i32(i32 %96)
%98 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_-203836479, i32 0, i32 2
%100 = load ptr, ptr %98
%102 = call i32 %100(%Point* %84)
call void @println_i32(i32 %102)
%104 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_-203836479, i32 0, i32 3
%106 = load ptr, ptr %104
%108 = call i32 %106(%Point* %84)
call void @println_i32(i32 %108)
%110 = alloca [8 x i8], align 1
%112 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 0
store i8 103, i8* %112
%114 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 1
store i8 97, i8* %114
%116 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 2
store i8 98, i8* %116
%118 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 3
store i8 114, i8* %118
%120 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 4
store i8 105, i8* %120
%122 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 5
store i8 101, i8* %122
%124 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 6
store i8 108, i8* %124
%126 = getelementptr inbounds [8 x i8], [8 x i8]* %110, i32 0, i32 7
store i8 0, i8* %126
%128 = call i32 @str_length(i8* %112)
call void @println_i32(i32 %128)
%130 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_-1450239945, i32 0, i32 2
%132 = load ptr, ptr %130
%134 = call i32 %132(i8* %112)
call void @println_i32(i32 %134)
%136 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_-1363293935, i32 0, i32 2
%138 = load ptr, ptr %136
%140 = call i32 %138(i8* %112)
call void @println_i32(i32 %140)
%142 = alloca [26 x i8], align 1
%144 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 0
store i8 65, i8* %144
%146 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 1
store i8 98, i8* %146
%148 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 2
store i8 111, i8* %148
%150 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 3
store i8 118, i8* %150
%152 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 4
store i8 101, i8* %152
%154 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 5
store i8 32, i8* %154
%156 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 6
store i8 105, i8* %156
%158 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 7
store i8 115, i8* %158
%160 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 8
store i8 32, i8* %160
%162 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 9
store i8 108, i8* %162
%164 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 10
store i8 101, i8* %164
%166 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 11
store i8 110, i8* %166
%168 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 12
store i8 103, i8* %168
%170 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 13
store i8 116, i8* %170
%172 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 14
store i8 104, i8* %172
%174 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 15
store i8 32, i8* %174
%176 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 16
store i8 111, i8* %176
%178 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 17
store i8 102, i8* %178
%180 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 18
store i8 32, i8* %180
%182 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 19
store i8 115, i8* %182
%184 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 20
store i8 116, i8* %184
%186 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 21
store i8 114, i8* %186
%188 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 22
store i8 105, i8* %188
%190 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 23
store i8 110, i8* %190
%192 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 24
store i8 103, i8* %192
%194 = getelementptr inbounds [26 x i8], [26 x i8]* %142, i32 0, i32 25
store i8 0, i8* %194
call void @println_str(i8* %144)
%196 = call i8* @malloc(i32 4)
call void @memset(i8* %196, i32 0, i32 4)
%198 = bitcast i8* %196 to %Map*
%200 = getelementptr inbounds %Map, %Map* %198, i32 0, i32 0
store i32 42, i32* %200
%202 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_-1736833064, i32 0, i32 2
%204 = load ptr, ptr %202
%206 = call i32 %204(%Map* %198)
call void @println_i32(i32 %206)
ret void
}