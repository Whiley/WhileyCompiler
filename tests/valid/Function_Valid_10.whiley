import whiley.lang.System

constant fcode is {1, 2, 3, 4}

constant tcode is {1, 2}

function g(fcode f) => string:
    return Any.toString(f)

method main(System.Console sys) => void:
    int x = 1
    sys.out.println(g(x))
