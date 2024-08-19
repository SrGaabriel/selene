@format_f = private unnamed_addr constant [3 x i8] c"%f\00"
@format_n = private unnamed_addr constant [3 x i8] c"%d\00"
@format_s = private unnamed_addr constant [3 x i8] c"%s\00"
declare i32 @printf(i8*, ...)
declare double @sqrt(double)
declare double @asin(double)
declare double @tan(double)
declare double @cos(double)
declare double @sin(double)
declare i32 @str_length(i8*)
declare void @println_f64(double)
declare void @println_bool(i1)
declare void @println_i32(i32)
declare void @println_str(i8*)
declare i8* @readln()
define void @main() {
entry:
%0 = fadd double 2.0, 0.0
%1 = call double @cos(double %0)
%2 = fadd double 2.0, 0.0
%3 = call double @sin(double %2)
%4 = fmul double %1, %3
call void @println_f64(double %4)
ret void
}