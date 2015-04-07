import whiley.lang.System

function indexOf([int] items, int ch) -> (int r)
ensures r == |items| || items[r] == ch:
    //
    int i = 0
    //
    while i < |items| && items[i] != ch
    where 0 <= i && i <= |items|:
        i = i + 1
    //
    return i

method main(System.Console console):
    for c in "hello world":
        console.out.println(indexOf("hello world",c))

