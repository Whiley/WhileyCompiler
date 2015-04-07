import whiley.lang.*

function indexOf([int] items, int item) -> (int r)
ensures r == |items| || items[r] == item:
    //
    int i = 0
    //
    while i < |items| where i >= 0 && i <= |items|:
        if items[i] == item:
            break    
        i = i + 1
    //
    return i

method main(System.Console console):
    int i = indexOf([1,2,3,4],3)
    console.out.println_s("indexOf = " ++ Any.toString(i))
