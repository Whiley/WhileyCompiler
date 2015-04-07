import whiley.lang.*

type nat is (int x) where x >= 0

type tnat is (nat, nat)

function f(tnat tup) -> nat:
    int x, int y = tup
    return x + y

public method main(System.Console console) -> void:
    (int,int) x = 3, 5
    console.out.println_s("GOT: " ++ Any.toString(f(x)))
