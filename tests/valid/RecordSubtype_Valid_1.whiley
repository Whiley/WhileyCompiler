

type R1 is {null | int x}

type R2 is {int x}

type R3 is {null x}

type R4 is R2 | R3

function f(R1 x) -> R4:
    return x

public export method test() :
    R4 z1 = f({x: 1})
    R4 z2 = f({x: null})
    assume z1 == {x: 1}
    assume z2 == {x: null}
