type R1 is {int x}
type R2 is {int y, int x}

function f(R1 r1, R2 r2) -> bool:
    return r1 == r2

method main() -> bool:
    R1 r1 = {x: 1}
    R2 r2 = {y: 2, x: 1}
    return f(r1,r2)
