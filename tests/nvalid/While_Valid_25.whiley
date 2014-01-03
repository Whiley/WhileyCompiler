import println from whiley.lang.System

function f(int n) => int:
    x = 0
    y = 0
    while x < n where y == (2 * x):
        x = x + 1
        y = y + 2
    return x + y

method main(System.Console console) => void:
    console.out.println(f(10))
