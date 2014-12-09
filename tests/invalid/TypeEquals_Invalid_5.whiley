import whiley.lang.*

type pos is (int p) where p > 0

type rec1 is {any f1, pos f2}

type rec2 is {int f1, int f2}

type rec12 is rec1 | rec2

function f(rec12 x) -> int:
    return 1

public method main(System.Console console) -> void:
    r1 = {f1: "hello", f2: 2}
    r2 = {f1: 1, f2: 0}
    r3 = {f1: "hello", f2: 0}
    console.out.println(f(r1))
    console.out.println(f(r2))
    console.out.println(f(r3))
