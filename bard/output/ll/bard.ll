@trait_-525258226 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @string_fooat
}>, align 8
@trait_-1465879958 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Map_fooat
}>, align 8
@trait_-1530583709 = external constant <{ i16, i16, ptr }>
@format_b = private unnamed_addr constant [3 x i8] c"%d\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare i32 @str_length(i8*)
declare i8* @readln()
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
declare i32 @strcmp(i8*, i8*)
%Map = type { i32 }
define i32 @Map_fooat(%Map* %1) {
entry:
%2 = getelementptr inbounds %Map, %Map* %1, i32 0, i32 0
%3 = load i32, i32* %2
ret i32 %3
}
define i32 @string_fooat(i8** %4) {
entry:
%5 = alloca [9 x i8], align 1
%6 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 0
store i8 45, i8* %6
%7 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 1
store i8 61, i8* %7
%8 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 2
store i8 45, i8* %8
%9 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 3
store i8 61, i8* %9
%10 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 4
store i8 45, i8* %10
%11 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 5
store i8 61, i8* %11
%12 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 6
store i8 45, i8* %12
%13 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 7
store i8 61, i8* %13
%14 = getelementptr inbounds [9 x i8], [9 x i8]* %5, i32 0, i32 8
store i8 0, i8* %14
call void @println_str(i8* %6)
call void @println_str(i8** %4)
%17 = alloca [9 x i8], align 1
%18 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 0
store i8 45, i8* %18
%19 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 1
store i8 61, i8* %19
%20 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 2
store i8 45, i8* %20
%21 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 3
store i8 61, i8* %21
%22 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 4
store i8 45, i8* %22
%23 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 5
store i8 61, i8* %23
%24 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 6
store i8 45, i8* %24
%25 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 7
store i8 61, i8* %25
%26 = getelementptr inbounds [9 x i8], [9 x i8]* %17, i32 0, i32 8
store i8 0, i8* %26
call void @println_str(i8* %18)
%28 = add i32 42, 0
ret i32 %28
}
define void @main() {
entry:
%29 = alloca [15 x i8], align 1
%30 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 0
store i8 69, i8* %30
%31 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 1
store i8 110, i8* %31
%32 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 2
store i8 116, i8* %32
%33 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 3
store i8 101, i8* %33
%34 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 4
store i8 114, i8* %34
%35 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 5
store i8 32, i8* %35
%36 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 6
store i8 97, i8* %36
%37 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 7
store i8 32, i8* %37
%38 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 8
store i8 119, i8* %38
%39 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 9
store i8 111, i8* %39
%40 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 10
store i8 114, i8* %40
%41 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 11
store i8 100, i8* %41
%42 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 12
store i8 58, i8* %42
%43 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 13
store i8 32, i8* %43
%44 = getelementptr inbounds [15 x i8], [15 x i8]* %29, i32 0, i32 14
store i8 0, i8* %44
call i1 @printf(i8* %30)
%46 = call i8* @readln()
%47 = alloca [5 x i8], align 1
%48 = getelementptr inbounds [5 x i8], [5 x i8]* %47, i32 0, i32 0
store i8 108, i8* %48
%49 = getelementptr inbounds [5 x i8], [5 x i8]* %47, i32 0, i32 1
store i8 117, i8* %49
%50 = getelementptr inbounds [5 x i8], [5 x i8]* %47, i32 0, i32 2
store i8 99, i8* %50
%51 = getelementptr inbounds [5 x i8], [5 x i8]* %47, i32 0, i32 3
store i8 107, i8* %51
%52 = getelementptr inbounds [5 x i8], [5 x i8]* %47, i32 0, i32 4
store i8 0, i8* %52
%53 = call i32 @strcmp(i8* %46, i8* %48)
%54 = icmp eq i32 %53, 0
br i1 %54, label %label0, label %label1
label0:
%55 = alloca [15 x i8], align 1
%56 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 0
store i8 89, i8* %56
%57 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 1
store i8 111, i8* %57
%58 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 2
store i8 117, i8* %58
%59 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 3
store i8 32, i8* %59
%60 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 4
store i8 97, i8* %60
%61 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 5
store i8 114, i8* %61
%62 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 6
store i8 101, i8* %62
%63 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 7
store i8 32, i8* %63
%64 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 8
store i8 108, i8* %64
%65 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 9
store i8 117, i8* %65
%66 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 10
store i8 99, i8* %66
%67 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 11
store i8 107, i8* %67
%68 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 12
store i8 121, i8* %68
%69 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 13
store i8 33, i8* %69
%70 = getelementptr inbounds [15 x i8], [15 x i8]* %55, i32 0, i32 14
store i8 0, i8* %70
call void @println_str(i8* %56)
br label %label2
label1:
%72 = alloca [19 x i8], align 1
%73 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 0
store i8 89, i8* %73
%74 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 1
store i8 111, i8* %74
%75 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 2
store i8 117, i8* %75
%76 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 3
store i8 32, i8* %76
%77 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 4
store i8 97, i8* %77
%78 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 5
store i8 114, i8* %78
%79 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 6
store i8 101, i8* %79
%80 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 7
store i8 32, i8* %80
%81 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 8
store i8 110, i8* %81
%82 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 9
store i8 111, i8* %82
%83 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 10
store i8 116, i8* %83
%84 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 11
store i8 32, i8* %84
%85 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 12
store i8 108, i8* %85
%86 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 13
store i8 117, i8* %86
%87 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 14
store i8 99, i8* %87
%88 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 15
store i8 107, i8* %88
%89 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 16
store i8 121, i8* %89
%90 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 17
store i8 33, i8* %90
%91 = getelementptr inbounds [19 x i8], [19 x i8]* %72, i32 0, i32 18
store i8 0, i8* %91
call void @println_str(i8* %73)
br label %label2
label2:
%93 = alloca [14 x i8], align 1
%94 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 0
store i8 72, i8* %94
%95 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 1
store i8 101, i8* %95
%96 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 2
store i8 108, i8* %96
%97 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 3
store i8 108, i8* %97
%98 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 4
store i8 111, i8* %98
%99 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 5
store i8 44, i8* %99
%100 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 6
store i8 32, i8* %100
%101 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 7
store i8 87, i8* %101
%102 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 8
store i8 111, i8* %102
%103 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 9
store i8 114, i8* %103
%104 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 10
store i8 108, i8* %104
%105 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 11
store i8 100, i8* %105
%106 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 12
store i8 33, i8* %106
%107 = getelementptr inbounds [14 x i8], [14 x i8]* %93, i32 0, i32 13
store i8 0, i8* %107
call void @println_str(i8* %94)
%109 = call i32 @str_length(i8* %46)
call void @println_i32(i32 %109)
%111 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_-1530583709, i32 0, i32 2
%112 = load ptr, ptr %111
%113 = call i32 %112(i8* %46)
call void @println_i32(i32 %113)
%115 = alloca [26 x i8], align 1
%116 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 0
store i8 65, i8* %116
%117 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 1
store i8 98, i8* %117
%118 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 2
store i8 111, i8* %118
%119 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 3
store i8 118, i8* %119
%120 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 4
store i8 101, i8* %120
%121 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 5
store i8 32, i8* %121
%122 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 6
store i8 105, i8* %122
%123 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 7
store i8 115, i8* %123
%124 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 8
store i8 32, i8* %124
%125 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 9
store i8 108, i8* %125
%126 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 10
store i8 101, i8* %126
%127 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 11
store i8 110, i8* %127
%128 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 12
store i8 103, i8* %128
%129 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 13
store i8 116, i8* %129
%130 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 14
store i8 104, i8* %130
%131 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 15
store i8 32, i8* %131
%132 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 16
store i8 111, i8* %132
%133 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 17
store i8 102, i8* %133
%134 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 18
store i8 32, i8* %134
%135 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 19
store i8 115, i8* %135
%136 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 20
store i8 116, i8* %136
%137 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 21
store i8 114, i8* %137
%138 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 22
store i8 105, i8* %138
%139 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 23
store i8 110, i8* %139
%140 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 24
store i8 103, i8* %140
%141 = getelementptr inbounds [26 x i8], [26 x i8]* %115, i32 0, i32 25
store i8 0, i8* %141
call void @println_str(i8* %116)
%143 = call i8* @malloc(i32 4)
call void @memset(i8* %143, i32 0, i32 4)
%144 = bitcast i8* %143 to %Map*
%145 = getelementptr inbounds %Map, %Map* %144, i32 0, i32 0
store i32 42, i32* %145
%146 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_-1465879958, i32 0, i32 2
%147 = load ptr, ptr %146
%148 = call i32 %147(%Map* %144)
call void @println_i32(i32 %148)
ret void
}