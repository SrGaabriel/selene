# 🐉 gwydion

---

This is a project I created to learn more about low-level programming and delve into the world of compilers. The goal is to create a simple programming language that compiles to LLVM IR.

You may look at the code and think the means to my desired end are incorrect and/or inefficient. That's not a bug, but a feature, as they say in San Francisco. I'm doing this to learn and mess around, so I won't hesitate to break stuff if it means I'll learn something new.

---

# Example

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