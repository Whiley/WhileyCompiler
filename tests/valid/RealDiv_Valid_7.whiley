

function diver(real x, real y, real z) -> real
requires y != 0.0 && z != 0.0:
    //
    return (x / y) / z

public export method test() :
    assume diver(1.2, 3.4, 4.5) == (4.0/51.0)
    assume diver(1000.0, 300.0, 400.0) == (1.0/120.0)


