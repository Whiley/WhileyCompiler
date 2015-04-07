import whiley.lang.System

function sum([int] items) -> (int result)
requires |items| > 0
ensures result >= 0:
    //
    int r = 0 
    for x in items where r >= 0:
        if x >= 0:
            r = r + x
    return r

public method main(System.Console console):
    console.out.println(sum([1,2,3]))
    console.out.println(sum([1,-1,2,3]))
    console.out.println(sum([1,-1,2,-2,3]))
