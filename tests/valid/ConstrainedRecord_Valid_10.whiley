import whiley.lang.System

type nat is (int x) where x >= 0

function f({int f} v) -> (int r)
ensures r >= 0:
    //
    if v is {nat f}:
        return v.f
    //
    return 0

method main(System.Console console):
    console.out.println(f({f:1}))
    console.out.println(f({f:-1}))
