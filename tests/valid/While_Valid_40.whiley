

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


public export method test():
    [int] ls = [1,2,3,4]
    assume contains(ls,0) == false
    assume contains(ls,1) == true
    assume contains(ls,2) == true
    assume contains(ls,3) == true
    assume contains(ls,4) == true
    assume contains(ls,5) == false
