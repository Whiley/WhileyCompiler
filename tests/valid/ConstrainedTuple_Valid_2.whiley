import whiley.lang.*
type nat is (int x) where x >= 0

type tup is (int, int)

function f(tup t) -> int:
    if t is (nat,nat):
        nat x, nat y = t
        return x + y
    //
    return 0

public method main(System.Console console) -> void:
    (int,int) x = 3, 5
    console.out.println(f(x))
    x = -3, 5
    console.out.println(f(x))
    x = 3, -5
    console.out.println(f(x))
