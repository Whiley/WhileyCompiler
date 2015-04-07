import whiley.lang.*

type nat is (int x) where x >= 0

function search([int] items, int item) -> (null|nat result)
// The input list must be in sorted order
requires all { i in 0 .. |items|-1 | items[i] < items[i+1] }
// If the answer is an integer, then it must be a value index
ensures (result != null) ==> items[result] == item
// If the answer is null, then the item must not be contained
ensures (result == null) ==> no { i in items | i == item }:
    //
    int i = 0
    while i < |items|
        where i >= 0 && i <= |items|
        where all { j in 0 .. i | items[j] != item }:
        //
        if items[i] == item:
            return i
        i = i + 1
    //
    return null

method main(System.Console console):
    [int] list = [3,5,6,9]
    console.out.println(search(list,0))
    console.out.println(search(list,1))
    console.out.println(search(list,2))
    console.out.println(search(list,3))
    console.out.println(search(list,4))
    console.out.println(search(list,5))
    console.out.println(search(list,6))
    console.out.println(search(list,7))
    console.out.println(search(list,8))
    console.out.println(search(list,9))
    console.out.println(search(list,10))
