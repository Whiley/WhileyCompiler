

type R1 is {int y, real x}

type R2 is {real y, int x}

type R3 is {int y, int x}

function f(R3 i) -> R1 | R2:
    return (R1) i

public export method test() :
    assume f({y: 123, x: 123542}) == {y: 123, x: 123542.0}
