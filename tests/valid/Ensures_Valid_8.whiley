import whiley.lang.*

function abs(int x) -> (int r)
ensures r >= 0:
    //
    if x < 0:
        x = -x
    //
    return x

method main(System.Console console):
    console.out.println_s("abs(1) = " ++ Any.toString(abs(1)))
    console.out.println_s("abs(-1) = " ++ Any.toString(abs(-1)))
