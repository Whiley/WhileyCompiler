import println from whiley.lang.System

function g(int x, int y) => int:
    return x + y

function f1(int x) => int(int):
    //
    return &(int y => g(x, y))

function f2(int y) => int(int):
    //
    return &(int x => g(x, y))

public method main(System.Console console) => void:
    fx = f1(10)
    fy = f2(20)
    console.out.println(fx(1))
    console.out.println(fy(1))
