import whiley.lang.System

function suber(real x, real y, real z) => real:
    return (x - y) - z

method main(System.Console sys) => void:
    sys.out.println(Any.toString(suber(1.2, 3.4, 4.5)))
    sys.out.println(Any.toString(suber(1000, 300, 400)))
