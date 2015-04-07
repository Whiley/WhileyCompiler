import whiley.lang.*

type nat is (int x) where x >= 0

function abs(int x) -> (int|null r)
// if we return an int, it cannot be negative
ensures r is int ==> r >= 0:
    //
    if x >= 0:
        return x
    else:
        return null

method main(System.Console console):
    console.out.println_s("abs(1) = " ++ Any.toString(abs(1)))
    console.out.println_s("abs(-1) = " ++ Any.toString(abs(-1)))
