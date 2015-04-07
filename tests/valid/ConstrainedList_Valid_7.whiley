import whiley.lang.*

function f([int] x) -> [int]
requires x[0] == 0:
    assert x[0] == 0
    return x

function g([int] x) -> [int]
requires x[0] == 0:
    assert |x| > 0
    return x

method main(System.Console console) -> void:
    console.out.println(f([0, 1, 2]))
    console.out.println(g([0]))
    console.out.println(g([0, 1, 2]))
