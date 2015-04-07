import whiley.lang.System

type nat is (int x) where x >= 0

method f(bool|int v) -> (int r)
ensures r >= 0:
    //
    if v is bool|nat:
        return 1
    //
    return 0

method main(System.Console console):
    console.out.println(f(1))
    console.out.println(f(true))
    console.out.println(f(-1))
