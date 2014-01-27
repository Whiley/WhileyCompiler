import println from whiley.lang.System

type nat is (int x) where x >= 0

type tnat is (nat, nat)

function f(tnat tup) => nat:
    (x, y) = tup
    return x + y

public method main(System.Console console) => void:
    x = (3, 5)
    console.out.println("GOT: " ++ f(x))
