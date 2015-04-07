import whiley.lang.*

function lastIndexOf([int] xs, int x) -> (int r)
// Return value is either -1 or a valid index in xs.
// Here, -1 indicates element was not found in list
ensures r >= -1 && r < |xs|
// If return is not -1 then the element at that index matches
ensures r >= 0 ==> xs[r] == x:
    //
    int i = 0
    int last = -1
    //
    while i < |xs|
    // i is positive and last is between -1 and size of xs
    where i >= 0 && last >= -1 && last < |xs|
    // If last is not negative, then the element at that index matches
    where last >= 0 ==> xs[last] == x:
        //
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
