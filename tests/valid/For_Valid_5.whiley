public function contains(int[] items, int item) -> (bool r)
ensures r <==> some { k in 0..|items| | items[k] == item }:
    //
    for i in 0..|items|
    // Nothing matches so far
    where all { k in 0..i | items[k] != item }:
        if items[i] != item:
            continue
        return true
    // Done
    return false
    
public export method test():
    assert !contains([],1)
    assert !contains([0],1)
    assert !contains([0,2],1)
    assume contains([1],1)
    assume contains([1,2],1)
    assume contains([1,2,3],1)
    assume contains([2,1],1)
    assume contains([3,2,1],1)
    assume contains([3,1,2],1)
    