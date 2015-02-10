type nat is (int x) where x >= 0

function f(int v) -> (int r)
ensures r >= 0:
    //
    if v is !nat:
        return 0
    //
    return v+1

method main(System.Console console):
    console.out.println(f(1))
    console.out.println(f(9))
    console.out.println(f(-1))
    console.out.println(f(-3))
