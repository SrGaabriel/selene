# 🐉 gwydion

---

This is a project I created to learn more about low-level programming and delve into the world of compilers. The goal is to create a simple programming language that compiles to LLVM IR.

You may look at the code and think the means to my desired end are incorrect and/or inefficient. That's not a bug, but a feature, as they say in San Francisco. I'm doing this to learn and mess around, so I won't hesitate to break stuff if it means I'll learn something new.

---

# Example

```go
func main () {
    reading := readln();
    println("You typed: " + reading);

    check_if_typed_banana(reading);
}

func check_if_typed_banana (reading: string) {
    if reading == "banana" {
        println("You typed banana!");
    } else {
        println("You didn't type banana.");
    }
}
```