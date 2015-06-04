type R1 is {int y, real x}

type R2 is {real y, int x}

type R3 is {int y, int x}

function f(R1 | R2 i) -> R3:
    return (R3) i
