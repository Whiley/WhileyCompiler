

function suber(real x, real y, real z) -> real:
    return (x - y) - z

public export method test() :
    assume suber(1.2, 3.4, 4.5) == -6.7
    assume suber(1000.0, 300.0, 400.0) == 300.0


