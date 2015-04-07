import whiley.lang.*

function lastIndexOf([int] xs, int x) -> (int|null r)
ensures r is int ==> xs[r] == x:
    //
    int i = 0
    int last = -1
    //
    while i < |xs| where i >= 0 && last >= -1 && last < |xs| && (last == -1 || xs[last] == x):
        if xs[i] == x:
            last = i
        i = i + 1
    //
    if last == -1:
        return null
    else:
        return last

method main(System.Console console):
    [int] list = [1,2,1,3,1,2]
    for i in 0 .. 3:
        int|null li = lastIndexOf(list,i)
        console.out.println_s("lastIndexOf(" ++ Any.toString(list) ++ "," ++ Any.toString(i) ++ ") = " ++ Any.toString(li))
