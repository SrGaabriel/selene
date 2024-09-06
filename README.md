# 🍂 selene

---

Selene is a statically-typed, imperative and procedural programming language compiled to LLVM IR.

The language is designed to be as easy to grasp as possible, while still providing a powerful set of features. It aims to combine the best of all worlds, providing a simple and easy-to-understand syntax, while still being powerful enough to be used in real-world applications.

---

# Examples

Basic input/output:
```go
func main () {
    check_typed();
}

func check_if_typed_apple(reading: string) {
    variable := "apple";
    if reading == variable {
        println("You typed 'apple'");
    } else {
        println("You didn't type 'apple'.");
        check_typed();
    }
}

func check_typed() {
    println("");
    println("Type something: ");

    reading := readln();
    println("You typed $reading");
    
    check_if_typed_apple(reading);
}
```

Structs:
```go
data Point(
    x: int32,
    y: int32,
    signed: bool
);

func main() {
    mut point := new_point();
    print_point(point);
    change_point(point);
    print_point(point);
}

func new_point() :: Point {
    return @Point(100, 400, true);
}

func change_point(point: mut Point) {
    point.x = 200;
    point.y = 800;
    point.signed = false;
}

func print_point(point: Point) {
    println(point.x);
    println(point.y);
    println(point.signed);
}
```

Traits:
```golang
data Map(
    foo: int32,
    remainder: int32
)

trait Fooable(
    new(foo: int32) :: Map,
    fooat(self) :: int32
)

make Map into Fooable {
    func new(foo: int32) :: Map {
        return @Map(foo, 42);
    }

    func fooat(self) :: int32 {
        return self.foo;
    }
}

func main() {
    println("Hello, World!");

    new := Map@new(4);
    println(new.fooat());

    map := @Map(42, 64);
    println(map.fooat());

    new2 := map.new(8);
    println(new2.fooat());
}
```

Lambdas:
```golang
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
```

---

# Brand

The brand of the language is a leaf. The theme color is #f05133.