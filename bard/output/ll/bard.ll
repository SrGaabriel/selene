@formatln_int32 = unnamed_addr constant [4 x i8] c"%d
\00"
declare i32 @printf(i8*, ...)
@str_1613095350 = unnamed_addr constant [14 x i8] c"Hello, World!\00"

define i32 @test() {
  ret i32 8
}
define i32 @main() {
  %1 = call i32 @test()
  %2 = getelementptr [4 x i8], [4 x i8]* @formatln_int32, i32 0, i32 0
  call i32 @printf(i8* %2, i32 %1)
  call i32 @printf(i8* %2, i32 %1)
  call i32 @printf(i8* %2, i32 %1)
  %6 = getelementptr [14 x i8], [14 x i8]* @str_1613095350, i32 0, i32 0
  call i32 @printf(i8* %6)
  ret i32 %1
}