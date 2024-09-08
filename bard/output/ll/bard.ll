@formatln_int32 = unnamed_addr constant [4 x i8] c"%d
\00"
declare i32 @printf(i8*, ...)
@str_1785507932 = unnamed_addr constant [14 x i8] c"Hello, World!\00"

define i32 @test() {
  ret i32 8
}
define i32 @main() {
  %1 = call i32 @test()
  %2 = getelementptr [4 x i8], [4 x i8]* @formatln_int32, i32 0, i32 0
  call i32 @printf(i8* %2, i32 %1)
  %4 = call i32 @test()
  %5 = getelementptr [4 x i8], [4 x i8]* @formatln_int32, i32 0, i32 0
  call i32 @printf(i8* %5, i32 %4)
  %7 = call i32 @test()
  %8 = getelementptr [4 x i8], [4 x i8]* @formatln_int32, i32 0, i32 0
  call i32 @printf(i8* %8, i32 %7)
  %10 = getelementptr [14 x i8], [14 x i8]* @str_1785507932, i32 0, i32 0
  call i32 @printf(i8* %10)
  %12 = call i32 @test()
  ret i32 %12
}