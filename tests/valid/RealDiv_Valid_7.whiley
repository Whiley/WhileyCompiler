import whiley.lang.*

function diver(real x, real y, real z) -> real
requires y != 0.0 && z != 0.0:
    //
    return (x / y) / z

method main(System.Console sys) -> void:
    sys.out.println(diver(1.2, 3.4, 4.5))
    sys.out.println(diver(1000.0, 300.0, 400.0))
