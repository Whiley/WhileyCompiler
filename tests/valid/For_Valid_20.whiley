

function find([int] items, int item) -> (int result)
ensures result == -1 || result in items:
    //
    int r = 0 
    for x in items where r >= 0:
        if x == item:
            return x
    return -1

public export method test():
    assume find([1,2,3],1) == 1
    assume find([1,2,3],2) == 2
    assume find([1,2,3],3) == 3
    assume find([1,2,3],4) == -1
