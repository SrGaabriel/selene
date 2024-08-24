@trait_57913236 = unnamed_addr constant <{ i16, i16, ptr }> <{
    i16 8,
    i16 8,
    ptr @Collection_next
}>, align 8
declare i32 @array_len(i32*)
declare i32 @str_length(i8*)
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
    %Collection = type { [7 x i8*]*, i32 }
define i8* @Collection_next(%Collection* %1) {
entry:
    %2 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 0
    %3 = load [7 x i8*]*, [7 x i8*]** %2
    %4 = getelementptr inbounds %Collection, %Collection* %1, i32 0, i32 1
    %5 = load i32, i32* %4
    %6 = getelementptr inbounds [7 x i8*], [7 x i8*]* %3, i32 0, i32 %5
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
    %15 = getelementptr inbounds i8*, i8** %13, i32 0, i32 0
    %16 = load i8, i8* %15
    call void @println_str(i8 %16)
    %18 = alloca [6 x i8], align 8
    %19 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 0
    store i8 72, i8* %19
    %20 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 1
    store i8 101, i8* %20
    %21 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 2
    store i8 108, i8* %21
    %22 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 3
    store i8 108, i8* %22
    %23 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 4
    store i8 111, i8* %23
    %24 = getelementptr inbounds [6 x i8], [6 x i8]* %18, i32 0, i32 5
    store i8 0, i8* %24
    %25 = alloca [6 x i8], align 8
    %26 = getelementptr inbounds [6 x i8], [6 x i8]* %25, i32 0, i32 0
    store i8 87, i8* %26
    %27 = getelementptr inbounds [6 x i8], [6 x i8]* %25, i32 0, i32 1
    store i8 111, i8* %27
    %28 = getelementptr inbounds [6 x i8], [6 x i8]* %25, i32 0, i32 2
    store i8 114, i8* %28
    %29 = getelementptr inbounds [6 x i8], [6 x i8]* %25, i32 0, i32 3
    store i8 108, i8* %29
    %30 = getelementptr inbounds [6 x i8], [6 x i8]* %25, i32 0, i32 4
    store i8 100, i8* %30
    %31 = getelementptr inbounds [6 x i8], [6 x i8]* %25, i32 0, i32 5
    store i8 0, i8* %31
    %32 = alloca [5 x i8], align 8
    %33 = getelementptr inbounds [5 x i8], [5 x i8]* %32, i32 0, i32 0
    store i8 66, i8* %33
    %34 = getelementptr inbounds [5 x i8], [5 x i8]* %32, i32 0, i32 1
    store i8 97, i8* %34
    %35 = getelementptr inbounds [5 x i8], [5 x i8]* %32, i32 0, i32 2
    store i8 114, i8* %35
    %36 = getelementptr inbounds [5 x i8], [5 x i8]* %32, i32 0, i32 3
    store i8 100, i8* %36
    %37 = getelementptr inbounds [5 x i8], [5 x i8]* %32, i32 0, i32 4
    store i8 0, i8* %37
    %38 = alloca [12 x i8], align 8
    %39 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 0
    store i8 80, i8* %39
    %40 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 1
    store i8 114, i8* %40
    %41 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 2
    store i8 111, i8* %41
    %42 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 3
    store i8 103, i8* %42
    %43 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 4
    store i8 114, i8* %43
    %44 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 5
    store i8 97, i8* %44
    %45 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 6
    store i8 109, i8* %45
    %46 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 7
    store i8 109, i8* %46
    %47 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 8
    store i8 105, i8* %47
    %48 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 9
    store i8 110, i8* %48
    %49 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 10
    store i8 103, i8* %49
    %50 = getelementptr inbounds [12 x i8], [12 x i8]* %38, i32 0, i32 11
    store i8 0, i8* %50
    %51 = alloca [9 x i8], align 8
    %52 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 0
    store i8 76, i8* %52
    %53 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 1
    store i8 97, i8* %53
    %54 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 2
    store i8 110, i8* %54
    %55 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 3
    store i8 103, i8* %55
    %56 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 4
    store i8 117, i8* %56
    %57 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 5
    store i8 97, i8* %57
    %58 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 6
    store i8 103, i8* %58
    %59 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 7
    store i8 101, i8* %59
    %60 = getelementptr inbounds [9 x i8], [9 x i8]* %51, i32 0, i32 8
    store i8 0, i8* %60
    %61 = alloca [3 x i8], align 8
    %62 = getelementptr inbounds [3 x i8], [3 x i8]* %61, i32 0, i32 0
    store i8 73, i8* %62
    %63 = getelementptr inbounds [3 x i8], [3 x i8]* %61, i32 0, i32 1
    store i8 115, i8* %63
    %64 = getelementptr inbounds [3 x i8], [3 x i8]* %61, i32 0, i32 2
    store i8 0, i8* %64
    %65 = alloca [5 x i8], align 8
    %66 = getelementptr inbounds [5 x i8], [5 x i8]* %65, i32 0, i32 0
    store i8 67, i8* %66
    %67 = getelementptr inbounds [5 x i8], [5 x i8]* %65, i32 0, i32 1
    store i8 111, i8* %67
    %68 = getelementptr inbounds [5 x i8], [5 x i8]* %65, i32 0, i32 2
    store i8 111, i8* %68
    %69 = getelementptr inbounds [5 x i8], [5 x i8]* %65, i32 0, i32 3
    store i8 108, i8* %69
    %70 = getelementptr inbounds [5 x i8], [5 x i8]* %65, i32 0, i32 4
    store i8 0, i8* %70
    %71 = alloca [7 x i8*], align 8
    %72 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 0
    store [6 x i8]* %18, i8** %72
    %73 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 1
    store [6 x i8]* %25, i8** %73
    %74 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 2
    store [5 x i8]* %32, i8** %74
    %75 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 3
    store [12 x i8]* %38, i8** %75
    %76 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 4
    store [9 x i8]* %51, i8** %76
    %77 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 5
    store [3 x i8]* %61, i8** %77
    %78 = getelementptr inbounds [7 x i8*], [7 x i8*]* %71, i32 0, i32 6
    store [5 x i8]* %65, i8** %78
    %79 = alloca %Collection, align 8
    %80 = getelementptr inbounds %Collection, %Collection* %79, i32 0, i32 0
    store [7 x i8*]* %71, [7 x i8*]** %80
    %81 = getelementptr inbounds %Collection, %Collection* %79, i32 0, i32 1
    store i32 0, i32* %81
    %82 = alloca i32, align 4
    store i32 0, i32* %82
    %83 = add i32 7, 0
    %84 = sub i32 %83, 1
    br label %label0
label0:
    %86 = load i32, i32* %82
    %87 = icmp sle i32 %86, %84
    br i1 %87, label %label1, label %label2
label1:
    %88 = load i32, i32* %82
    %89 = alloca [15 x i8], align 8
    %90 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 0
    store i8 78, i8* %90
    %91 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 1
    store i8 111, i8* %91
    %92 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 2
    store i8 119, i8* %92
    %93 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 3
    store i8 32, i8* %93
    %94 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 4
    store i8 97, i8* %94
    %95 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 5
    store i8 116, i8* %95
    %96 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 6
    store i8 32, i8* %96
    %97 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 7
    store i8 105, i8* %97
    %98 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 8
    store i8 110, i8* %98
    %99 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 9
    store i8 100, i8* %99
    %100 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 10
    store i8 101, i8* %100
    %101 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 11
    store i8 120, i8* %101
    %102 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 12
    store i8 58, i8* %102
    %103 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 13
    store i8 32, i8* %103
    %104 = getelementptr inbounds [15 x i8], [15 x i8]* %89, i32 0, i32 14
    store i8 0, i8* %104
    call i1 @printf([15 x i8]* %89)
    call void @println_i32(i32 %88)
    %107 = getelementptr inbounds <{i16, i16, ptr}>, ptr @trait_57913236, i32 0, i32 2
    %108 = load ptr, ptr %107
    %109 = call i8* %108(%Collection* %79)
    call void @println_str(i8* %109)
    %111 = add i32 %86, 1
    store i32 %111, i32* %82
    br label %label0
label2:
    ret void
}