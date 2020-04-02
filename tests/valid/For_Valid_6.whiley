type nat is (int x) where x >= 0

function sum(nat[] items) -> nat:
    int r = 0
    for i in 0..|items| where r >= 0:
        r = r + items[i]
    // Done
    return (nat) r
    
public export method test():
    assume sum([1]) == 1
    assume sum([1,2]) == 3
    assume sum([1,2,3]) == 6
    assume sum([1,2,3,4,5,6]) == 21