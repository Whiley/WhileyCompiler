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
    assume f(1) == 1
    assume f(true) == 1
    assume f(-1) == 0
