import whiley.lang.System

constant cr2num is {1, 2, 3, 4}

function f(cr2num x) => string:
    int y = x
    return Any.toString(y)

method main(System.Console sys) => void:
    sys.out.println(f(3))
