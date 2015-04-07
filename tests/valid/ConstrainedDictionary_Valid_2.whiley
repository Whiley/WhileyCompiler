import whiley.lang.System

type nat is (int x) where x >= 0

function f({int=>int} v) -> (int r)
ensures r >= 0:
    //
    if v is {nat=>nat}:
        return |v|
    //
    return 0

method main(System.Console console):
    console.out.println(f({1=>1,2=>2,3=>3}))
    console.out.println(f({-1=>0}))
    console.out.println(f({0=>-1}))
    console.out.println(f({1=>1,0=>0,0=>-1}))
    console.out.println(f({1=>1,0=>0,-1=>0}))
