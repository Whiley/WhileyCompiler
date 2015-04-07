import whiley.lang.*

function f(int n) -> int:
    int x = 0
    int y = 0
    while x < n where y == (2 * x):
        x = x + 1
        y = y + 2
    return x + y

method main(System.Console console) -> void:
    console.out.println(f(10))
