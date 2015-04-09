import whiley.lang.System

function find([int] items, int item) -> (int result)
ensures result == -1 || result in items:
    //
    int r = 0 
    for x in items where r >= 0:
        if x == item:
            return x
    return -1

public method main(System.Console console):
    assume find([1,2,3],1) == 1
    assume find([1,2,3],2) == 2
    assume find([1,2,3],3) == 3
    assume find([1,2,3],4) == -1
