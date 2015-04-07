import whiley.lang.System

function lastIndexOf([int] items, int item) -> (int r)
requires |items| > 0
ensures r == -1 || items[r] == item:
    //
    int i = |items|
    do:
        i = i - 1
    while i >= 0 && items[i] != item 
        where i >= -1 && i < |items|
    //
    return i

method main(System.Console c):
    c.out.println(lastIndexOf([1,2,3,4,5,4,3,2,1],1))
    c.out.println(lastIndexOf([1,2,3,4,5,4,3,2,1],2))
    c.out.println(lastIndexOf([1,2,3,4,5,4,3,2,1],3))
    c.out.println(lastIndexOf([1,2,3,4,5,4,3,2,1],4))
    c.out.println(lastIndexOf([1,2,3,4,5,4,3,2,1],5))            
