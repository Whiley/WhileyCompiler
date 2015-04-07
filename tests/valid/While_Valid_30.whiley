import whiley.lang.*

function duplicate(int n) -> (int r)
requires n >= 0
ensures  r == 2*n:
    //
    int i = 0
    int r = 0
    while i < n where i <= n && r == 2*i:
        r = r + 2
        i = i + 1
    return r

method main(System.Console console):
    for i in 0 .. 10:
        console.out.println_s("GOT: " ++ Any.toString(duplicate(i)))

