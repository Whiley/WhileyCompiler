import whiley.lang.System

constant odd is {1, 3, 5}

constant even is {2, 4, 6}

constant oddeven is odd + even

function f(oddeven x) => even:
    if x in odd:
        return 2
    return x

method main(System.Console sys) => void:
    int y = 1
    y = f(1)
    sys.out.println(Any.toString(y))
