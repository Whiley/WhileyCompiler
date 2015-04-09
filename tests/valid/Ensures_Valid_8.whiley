import whiley.lang.*

function abs(int x) -> (int r)
ensures r >= 0:
    //
    if x < 0:
        x = -x
    //
    return x

method main(System.Console console):
    assume abs(1) == 1
    assume abs(-1) == 1
