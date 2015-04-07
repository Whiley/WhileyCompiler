import whiley.lang.*

function add([int] items, int n) -> [int]
requires n > 0:
    //
    int i = 0
    [int] oitems = items
    //
    while i < |items|
    //
    where i >= 0 && i <= |items| && |items| == |oitems|
    // Elements upto but not including i are zeroed
    where all { j in 0 .. i | oitems[j] < items[j] }:
        //
        items[i] = oitems[i] + n
        i = i + 1
    //
    return items


method main(System.Console console):
    [int] ls = [1,2,3,4]
    console.out.println_s("ADD(1) = " ++ Any.toString(add(ls,1)))
    console.out.println_s("ADD(11) = " ++ Any.toString(add(ls,11)))
