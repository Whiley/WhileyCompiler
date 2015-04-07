import whiley.lang.*

function lastIndexOf([int] xs, int x) -> (int|null r)
ensures r is int ==> xs[r] == x:
    //
    int i = 0
    int|null last = null
    //
    while i < |xs| where i >= 0 && (last is int ==> 0 <= last && last < |xs| && xs[last] == x):
        if xs[i] == x:
            last = i
        i = i + 1
    //
    return last

method main(System.Console console):
    [int] list = [1,2,1,3,1,2]
    for i in 0 .. 3:
        int|null li = lastIndexOf(list,i)
        console.out.println_s("lastIndexOf(" ++ Any.toString(list) ++ "," ++ Any.toString(i) ++ ") = " ++ Any.toString(li))
