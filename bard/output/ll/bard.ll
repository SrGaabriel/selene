@trait_1296205339 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Collection_next
}>, align 8
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare void @memset(i8*, i32, i32)
declare i8* @malloc(i32)
    %Collection = type { i8**, i32 }
define i8* @Collection_next(%Collection* %1) {
entry:
    %2 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 0
    %3 = load i8**, i8*** %2
    %4 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    %5 = load i32, i32* %4
    %6 = getelementptr inbounds i8*, i8** %3, i32 %5
    %7 = load i8*, i8** %6
    %8 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    %9 = load i32, i32* %8
    %10 = add i32 %9, 1
    %11 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    store i32 %10, i32* %11
    ret i8* %7
}
define void @main(i32 %12, i8** %13) {
entry:
    call void @println_i32(i32 %12)
    %15 = alloca [13 x i8], align 1
    %16 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 0
    store i8 89, i8* %16
    %17 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 1
    store i8 111, i8* %17
    %18 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 2
    store i8 117, i8* %18
    %19 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 3
    store i8 32, i8* %19
    %20 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 4
    store i8 112, i8* %20
    %21 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 5
    store i8 97, i8* %21
    %22 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 6
    store i8 115, i8* %22
    %23 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 7
    store i8 115, i8* %23
    %24 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 8
    store i8 101, i8* %24
    %25 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 9
    store i8 100, i8* %25
    %26 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 10
    store i8 58, i8* %26
    %27 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 11
    store i8 32, i8* %27
    %28 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 12
    store i8 0, i8* %28
    %29 = getelementptr inbounds [13 x i8], [13 x i8]* %15, i32 0, i32 0
    call i1 @printf(i8* %29)
    %31 = alloca i32, align 4
    store i32 1, i32* %31
    %32 = sub i32 %12, 1
    br label %label0
label0:
    %34 = load i32, i32* %31
    %35 = icmp sle i32 %34, %32
    br i1 %35, label %label1, label %label2
label1:
    %36 = load i32, i32* %31
    %37 = getelementptr inbounds i8*, i8** %13, i32 %36
    %38 = load i8*, i8** %37
    call i1 @printf(i8* %38)
    %40 = alloca [2 x i8], align 1
    %41 = getelementptr inbounds [2 x i8], [2 x i8]* %40, i32 0, i32 0
    store i8 32, i8* %41
    %42 = getelementptr inbounds [2 x i8], [2 x i8]* %40, i32 0, i32 1
    store i8 0, i8* %42
    %43 = getelementptr inbounds [2 x i8], [2 x i8]* %40, i32 0, i32 0
    call i1 @printf(i8* %43)
    %45 = add i32 %34, 1
    store i32 %45, i32* %31
    br label %label0
label2:
    %47 = call i8* @malloc(i32 64)
    call void @memset(i8* %47, i32 0, i32 64)
    call void @println_str(i8* %47)
    %49 = alloca [6 x i8], align 1
    %50 = getelementptr inbounds [6 x i8], [6 x i8]* %49, i32 0, i32 0
    store i8 72, i8* %50
    %51 = getelementptr inbounds [6 x i8], [6 x i8]* %49, i32 0, i32 1
    store i8 101, i8* %51
    %52 = getelementptr inbounds [6 x i8], [6 x i8]* %49, i32 0, i32 2
    store i8 108, i8* %52
    %53 = getelementptr inbounds [6 x i8], [6 x i8]* %49, i32 0, i32 3
    store i8 108, i8* %53
    %54 = getelementptr inbounds [6 x i8], [6 x i8]* %49, i32 0, i32 4
    store i8 111, i8* %54
    %55 = getelementptr inbounds [6 x i8], [6 x i8]* %49, i32 0, i32 5
    store i8 0, i8* %55
    %56 = alloca [6 x i8], align 1
    %57 = getelementptr inbounds [6 x i8], [6 x i8]* %56, i32 0, i32 0
    store i8 87, i8* %57
    %58 = getelementptr inbounds [6 x i8], [6 x i8]* %56, i32 0, i32 1
    store i8 111, i8* %58
    %59 = getelementptr inbounds [6 x i8], [6 x i8]* %56, i32 0, i32 2
    store i8 114, i8* %59
    %60 = getelementptr inbounds [6 x i8], [6 x i8]* %56, i32 0, i32 3
    store i8 108, i8* %60
    %61 = getelementptr inbounds [6 x i8], [6 x i8]* %56, i32 0, i32 4
    store i8 100, i8* %61
    %62 = getelementptr inbounds [6 x i8], [6 x i8]* %56, i32 0, i32 5
    store i8 0, i8* %62
    %63 = alloca [5 x i8], align 1
    %64 = getelementptr inbounds [5 x i8], [5 x i8]* %63, i32 0, i32 0
    store i8 66, i8* %64
    %65 = getelementptr inbounds [5 x i8], [5 x i8]* %63, i32 0, i32 1
    store i8 97, i8* %65
    %66 = getelementptr inbounds [5 x i8], [5 x i8]* %63, i32 0, i32 2
    store i8 114, i8* %66
    %67 = getelementptr inbounds [5 x i8], [5 x i8]* %63, i32 0, i32 3
    store i8 100, i8* %67
    %68 = getelementptr inbounds [5 x i8], [5 x i8]* %63, i32 0, i32 4
    store i8 0, i8* %68
    %69 = alloca [12 x i8], align 1
    %70 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 0
    store i8 80, i8* %70
    %71 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 1
    store i8 114, i8* %71
    %72 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 2
    store i8 111, i8* %72
    %73 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 3
    store i8 103, i8* %73
    %74 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 4
    store i8 114, i8* %74
    %75 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 5
    store i8 97, i8* %75
    %76 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 6
    store i8 109, i8* %76
    %77 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 7
    store i8 109, i8* %77
    %78 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 8
    store i8 105, i8* %78
    %79 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 9
    store i8 110, i8* %79
    %80 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 10
    store i8 103, i8* %80
    %81 = getelementptr inbounds [12 x i8], [12 x i8]* %69, i32 0, i32 11
    store i8 0, i8* %81
    %82 = alloca [9 x i8], align 1
    %83 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 0
    store i8 76, i8* %83
    %84 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 1
    store i8 97, i8* %84
    %85 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 2
    store i8 110, i8* %85
    %86 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 3
    store i8 103, i8* %86
    %87 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 4
    store i8 117, i8* %87
    %88 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 5
    store i8 97, i8* %88
    %89 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 6
    store i8 103, i8* %89
    %90 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 7
    store i8 101, i8* %90
    %91 = getelementptr inbounds [9 x i8], [9 x i8]* %82, i32 0, i32 8
    store i8 0, i8* %91
    %92 = alloca [3 x i8], align 1
    %93 = getelementptr inbounds [3 x i8], [3 x i8]* %92, i32 0, i32 0
    store i8 73, i8* %93
    %94 = getelementptr inbounds [3 x i8], [3 x i8]* %92, i32 0, i32 1
    store i8 115, i8* %94
    %95 = getelementptr inbounds [3 x i8], [3 x i8]* %92, i32 0, i32 2
    store i8 0, i8* %95
    %96 = alloca [5 x i8], align 1
    %97 = getelementptr inbounds [5 x i8], [5 x i8]* %96, i32 0, i32 0
    store i8 67, i8* %97
    %98 = getelementptr inbounds [5 x i8], [5 x i8]* %96, i32 0, i32 1
    store i8 111, i8* %98
    %99 = getelementptr inbounds [5 x i8], [5 x i8]* %96, i32 0, i32 2
    store i8 111, i8* %99
    %100 = getelementptr inbounds [5 x i8], [5 x i8]* %96, i32 0, i32 3
    store i8 108, i8* %100
    %101 = getelementptr inbounds [5 x i8], [5 x i8]* %96, i32 0, i32 4
    store i8 0, i8* %101
    %102 = call i8* @malloc(i32 64)
    call void @memset(i8* %102, i32 0, i32 64)
    %103 = bitcast i8* %102 to i8**
    %104 = getelementptr inbounds i8*, i8** %103, i32 0
    store [6 x i8]* %49, i8** %104
    %105 = getelementptr inbounds i8*, i8** %103, i32 1
    store [6 x i8]* %56, i8** %105
    %106 = getelementptr inbounds i8*, i8** %103, i32 2
    store [5 x i8]* %63, i8** %106
    %107 = getelementptr inbounds i8*, i8** %103, i32 3
    store [12 x i8]* %69, i8** %107
    %108 = getelementptr inbounds i8*, i8** %103, i32 4
    store [9 x i8]* %82, i8** %108
    %109 = getelementptr inbounds i8*, i8** %103, i32 5
    store [3 x i8]* %92, i8** %109
    %110 = getelementptr inbounds i8*, i8** %103, i32 6
    store [5 x i8]* %96, i8** %110
    %111 = alloca %Collection, align 8
    %112 = getelementptr inbounds %Collection, %Collection* %111, i32 0, i32 0
    store i8** %103, i8*** %112
    %113 = getelementptr inbounds %Collection, %Collection* %111, i32 0, i32 1
    store i32 0, i32* %113
    %114 = alloca i32, align 4
    store i32 0, i32* %114
    br label %label3
label3:
    %116 = load i32, i32* %114
    %117 = icmp sle i32 %116, 6
    br i1 %117, label %label4, label %label5
label4:
    %118 = load i32, i32* %114
    %119 = alloca [15 x i8], align 1
    %120 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 0
    store i8 78, i8* %120
    %121 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 1
    store i8 111, i8* %121
    %122 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 2
    store i8 119, i8* %122
    %123 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 3
    store i8 32, i8* %123
    %124 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 4
    store i8 97, i8* %124
    %125 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 5
    store i8 116, i8* %125
    %126 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 6
    store i8 32, i8* %126
    %127 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 7
    store i8 105, i8* %127
    %128 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 8
    store i8 110, i8* %128
    %129 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 9
    store i8 100, i8* %129
    %130 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 10
    store i8 101, i8* %130
    %131 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 11
    store i8 120, i8* %131
    %132 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 12
    store i8 58, i8* %132
    %133 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 13
    store i8 32, i8* %133
    %134 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 14
    store i8 0, i8* %134
    %135 = getelementptr inbounds [15 x i8], [15 x i8]* %119, i32 0, i32 0
    call i1 @printf(i8* %135)
    call void @println_i32(i32 %118)
    %138 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_1296205339, i32 0, i32 2
    %139 = load ptr, ptr %138
    %140 = call i8* %139(%Collection* %111)
    call void @println_str(i8* %140)
    %142 = add i32 %116, 1
    store i32 %142, i32* %114
    br label %label3
label5:
    ret void
}