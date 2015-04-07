import whiley.lang.*

function zeroOut([int] items) -> [int]:
    int i = 0
    [int] oitems = items
    //
    while i < |items|
    //
    where i >= 0 && i <= |items| && |items| == |oitems|
    // Elements upto but not including i are zeroed
    where all { j in 0 .. i | items[j] == 0 }:
        //
        items[i] = 0
        i = i + 1
    //
    return items


method main(System.Console console):
    [int] ls = [1,2,3,4]
    ls = zeroOut(ls)
    console.out.println_s("ZEROED: " ++ Any.toString(ls))
