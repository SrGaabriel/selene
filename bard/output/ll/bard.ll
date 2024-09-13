%Map = type { i32 }
@vtable_trait_-207835532 = unnamed_addr constant <{ ptr }> <{
  ptr @fooat_for_1239807799
}>
@formatln_int32 = unnamed_addr constant [4 x i8] c"%d
\00"
declare i32 @printf(i8*, ...)
@str_1498789909 = unnamed_addr constant [14 x i8] c"Hello, World!\00"
declare i32 @puts(i8*)

define i32 @fooat_for_1239807799() {
  ret i32 5
}
define i32 @test() {
  ret i32 8
}
define i32 @main() {
  %1 = call i32 @test()
  %2 = getelementptr [4 x i8], [4 x i8]* @formatln_int32, i32 0, i32 0
  call i32 @printf(i8* %2, i32 %1)
  call i32 @printf(i8* %2, i32 %1)
  call i32 @printf(i8* %2, i32 %1)
  %6 = getelementptr [14 x i8], [14 x i8]* @str_1498789909, i32 0, i32 0
  call i32 @puts(i8* %6)
  call i32 @puts(i8* %6)
  ret i32 %1
}
