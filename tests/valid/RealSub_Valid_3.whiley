import whiley.lang.*

function suber(real x, real y, real z) -> real:
    return (x - y) - z

method main(System.Console sys) -> void:
    sys.out.println(suber(1.2, 3.4, 4.5))
    sys.out.println(suber(1000.0, 300.0, 400.0))
