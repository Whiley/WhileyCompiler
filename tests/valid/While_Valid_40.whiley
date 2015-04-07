import whiley.lang.System

function contains([int] items, int item) -> (bool r)
ensures r ==> some { i in 0 .. |items| | item == items[i] }:
    //
    int i = 0
    //
    while i < |items| where i >= 0:
        if items[i] == item:
            return true
        i = i + 1
    //
    return false


method main(System.Console console):
    [int] ls = [1,2,3,4]
    for l in [3,5,1]:
        console.out.println(contains(ls,l))
