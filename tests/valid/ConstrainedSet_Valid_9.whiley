import whiley.lang.System

type nat is (int x) where x >= 0

function f({int} v) -> (int r)
ensures r >= 0:
    //
    if v is {nat}:
        return |v|
    //
    return 0

method main(System.Console console):
    console.out.println(f({1,2,3}))
    console.out.println(f({-1}))
    console.out.println(f({1,0,-1}))
