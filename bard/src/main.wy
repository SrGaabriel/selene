func callback(value: int32, scope: lambda(int32) -> int32) {
    result := scope(value);
    println(result);
}

func main() {
    println("Hello, World!");
    callback(2, lambda|x: int32| x + 12);
    callback(4, lambda|x: int32| x + 24);
    callback(8, lambda|x: int32| x + 36);
}