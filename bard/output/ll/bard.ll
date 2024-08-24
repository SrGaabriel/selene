declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@trait_1874442193 = external constant <{ i16, i16, ptr, ptr }>
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
%List = type { i8**, i32 }
define void @main() {
entry:
    %0 = alloca [8 x i8], align 1
    %1 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 0
    store i8 71, i8* %1
    %2 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 1
    store i8 119, i8* %2
    %3 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 2
    store i8 121, i8* %3
    %4 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 3
    store i8 100, i8* %4
    %5 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 4
    store i8 105, i8* %5
    %6 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 5
    store i8 111, i8* %6
    %7 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 6
    store i8 110, i8* %7
    %8 = getelementptr inbounds [8 x i8], [8 x i8]* %0, i32 0, i32 7
    store i8 0, i8* %8
    %9 = alloca [3 x i8], align 1
    %10 = getelementptr inbounds [3 x i8], [3 x i8]* %9, i32 0, i32 0
    store i8 73, i8* %10
    %11 = getelementptr inbounds [3 x i8], [3 x i8]* %9, i32 0, i32 1
    store i8 115, i8* %11
    %12 = getelementptr inbounds [3 x i8], [3 x i8]* %9, i32 0, i32 2
    store i8 0, i8* %12
    %13 = alloca [4 x i8], align 1
    %14 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 0
    store i8 84, i8* %14
    %15 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 1
    store i8 104, i8* %15
    %16 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 2
    store i8 101, i8* %16
    %17 = getelementptr inbounds [4 x i8], [4 x i8]* %13, i32 0, i32 3
    store i8 0, i8* %17
    %18 = alloca [6 x i8], align 1
    %19 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 0
    store i8 66, i8* %19
    %20 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 1
    store i8 101, i8* %20
    %21 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 2
    store i8 115, i8* %21
    %22 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 3
    store i8 116, i8* %22
    %23 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 4
    store i8 33, i8* %23
    %24 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 5
    store i8 0, i8* %24
    %25 = call i8* @malloc(i32 64)
    call void @memset(i8* %25, i32 0, i32 64)
    %26 = bitcast i8* %25 to i8**
    %27 = getelementptr inbounds i8*, i8** %26, i32 0
    store [8 x i8]* %0, i8** %27
    %28 = getelementptr inbounds i8*, i8** %26, i32 1
    store [3 x i8]* %9, i8** %28
    %29 = getelementptr inbounds i8*, i8** %26, i32 2
    store [4 x i8]* %13, i8** %29
    %30 = getelementptr inbounds i8*, i8** %26, i32 3
    store [6 x i8]* %18, i8** %30
    %32 = alloca %List, align 8
    %33 = getelementptr inbounds %List, %List* %32, i32 0, i32 0
    store i8** %26, i8*** %33
    %34 = getelementptr inbounds %List, %List* %32, i32 0, i32 1
    store i32 4, i32* %34
    %35 = alloca i32, align 4
    store i32 0, i32* %35
    %36 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1874442193, i32 0, i32 2
    %37 = load ptr, ptr %36
    %38 = call i32 %37(%List* %32)
    %39 = sub i32 %38, 1
    br label %label0
label0:
    %41 = load i32, i32* %35
    %42 = icmp sle i32 %41, %39
    br i1 %42, label %label1, label %label2
label1:
    %43 = load i32, i32* %35
    %44 = alloca [7 x i8], align 1
    %45 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 0
    store i8 78, i8* %45
    %46 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 1
    store i8 101, i8* %46
    %47 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 2
    store i8 120, i8* %47
    %48 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 3
    store i8 116, i8* %48
    %49 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 4
    store i8 58, i8* %49
    %50 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 5
    store i8 32, i8* %50
    %51 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 6
    store i8 0, i8* %51
    %52 = getelementptr inbounds [7 x i8], [7 x i8]* %44, i32 0, i32 0
    call i1 @printf(i8* %52)
    %55 = getelementptr inbounds i8*, i8** %26, i32 %43
    %56 = load i8*, i8** %55
    call void @println_str(i8* %56)
    %58 = add i32 %41, 1
    store i32 %58, i32* %35
    br label %label0
label2:
    %60 = alloca [17 x i8], align 1
    %61 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 0
    store i8 61, i8* %61
    %62 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 1
    store i8 61, i8* %62
    %63 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 2
    store i8 61, i8* %63
    %64 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 3
    store i8 61, i8* %64
    %65 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 4
    store i8 61, i8* %65
    %66 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 5
    store i8 61, i8* %66
    %67 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 6
    store i8 61, i8* %67
    %68 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 7
    store i8 61, i8* %68
    %69 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 8
    store i8 61, i8* %69
    %70 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 9
    store i8 61, i8* %70
    %71 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 10
    store i8 61, i8* %71
    %72 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 11
    store i8 61, i8* %72
    %73 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 12
    store i8 61, i8* %73
    %74 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 13
    store i8 61, i8* %74
    %75 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 14
    store i8 61, i8* %75
    %76 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 15
    store i8 61, i8* %76
    %77 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 16
    store i8 0, i8* %77
    %78 = getelementptr inbounds [17 x i8], [17 x i8]* %60, i32 0, i32 0
    call void @println_str(i8* %78)
    %80 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1874442193, i32 0, i32 3
    %81 = load ptr, ptr %80
    %82 = alloca [5 x i8], align 1
    %83 = getelementptr inbounds [5 x i8], [5 x i8]* %82, i32 0, i32 0
    store i8 66, i8* %83
    %84 = getelementptr inbounds [5 x i8], [5 x i8]* %82, i32 0, i32 1
    store i8 97, i8* %84
    %85 = getelementptr inbounds [5 x i8], [5 x i8]* %82, i32 0, i32 2
    store i8 114, i8* %85
    %86 = getelementptr inbounds [5 x i8], [5 x i8]* %82, i32 0, i32 3
    store i8 100, i8* %86
    %87 = getelementptr inbounds [5 x i8], [5 x i8]* %82, i32 0, i32 4
    store i8 0, i8* %87
    call i1 %81(%List* %32, [5 x i8]* %82)
    %89 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1874442193, i32 0, i32 3
    %90 = load ptr, ptr %89
    %91 = alloca [3 x i8], align 1
    %92 = getelementptr inbounds [3 x i8], [3 x i8]* %91, i32 0, i32 0
    store i8 73, i8* %92
    %93 = getelementptr inbounds [3 x i8], [3 x i8]* %91, i32 0, i32 1
    store i8 115, i8* %93
    %94 = getelementptr inbounds [3 x i8], [3 x i8]* %91, i32 0, i32 2
    store i8 0, i8* %94
    call i1 %90(%List* %32, [3 x i8]* %91)
    %96 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1874442193, i32 0, i32 3
    %97 = load ptr, ptr %96
    %98 = alloca [5 x i8], align 1
    %99 = getelementptr inbounds [5 x i8], [5 x i8]* %98, i32 0, i32 0
    store i8 67, i8* %99
    %100 = getelementptr inbounds [5 x i8], [5 x i8]* %98, i32 0, i32 1
    store i8 111, i8* %100
    %101 = getelementptr inbounds [5 x i8], [5 x i8]* %98, i32 0, i32 2
    store i8 111, i8* %101
    %102 = getelementptr inbounds [5 x i8], [5 x i8]* %98, i32 0, i32 3
    store i8 108, i8* %102
    %103 = getelementptr inbounds [5 x i8], [5 x i8]* %98, i32 0, i32 4
    store i8 0, i8* %103
    call i1 %97(%List* %32, [5 x i8]* %98)
    %105 = getelementptr inbounds <{i16, i16, ptr, ptr}>, ptr @trait_1874442193, i32 0, i32 3
    %106 = load ptr, ptr %105
    %107 = alloca [5 x i8], align 1
    %108 = getelementptr inbounds [5 x i8], [5 x i8]* %107, i32 0, i32 0
    store i8 84, i8* %108
    %109 = getelementptr inbounds [5 x i8], [5 x i8]* %107, i32 0, i32 1
    store i8 111, i8* %109
    %110 = getelementptr inbounds [5 x i8], [5 x i8]* %107, i32 0, i32 2
    store i8 111, i8* %110
    %111 = getelementptr inbounds [5 x i8], [5 x i8]* %107, i32 0, i32 3
    store i8 33, i8* %111
    %112 = getelementptr inbounds [5 x i8], [5 x i8]* %107, i32 0, i32 4
    store i8 0, i8* %112
    call i1 %106(%List* %32, [5 x i8]* %107)
    %114 = alloca i32, align 4
    store i32 0, i32* %114
    %115 = getelementptr inbounds %List, %List* %32, i32 0, i32 1
    %116 = load i32, i32* %115
    %117 = sub i32 %116, 1
    br label %label3
label3:
    %119 = load i32, i32* %114
    %120 = icmp sle i32 %119, %117
    br i1 %120, label %label4, label %label5
label4:
    %121 = load i32, i32* %114
    %122 = alloca [7 x i8], align 1
    %123 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 0
    store i8 78, i8* %123
    %124 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 1
    store i8 101, i8* %124
    %125 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 2
    store i8 120, i8* %125
    %126 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 3
    store i8 116, i8* %126
    %127 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 4
    store i8 58, i8* %127
    %128 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 5
    store i8 32, i8* %128
    %129 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 6
    store i8 0, i8* %129
    %130 = getelementptr inbounds [7 x i8], [7 x i8]* %122, i32 0, i32 0
    call i1 @printf(i8* %130)
    %133 = getelementptr inbounds i8*, i8** %26, i32 %121
    %134 = load i8*, i8** %133
    call void @println_str(i8* %134)
    %136 = add i32 %119, 1
    store i32 %136, i32* %114
    br label %label3
label5:
    ret void
}