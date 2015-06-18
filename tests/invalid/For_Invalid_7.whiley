
type fr6nat is int x where x >= 0

function g([fr6nat] xs) -> (int x)
ensures x > 1:
    //
    int r = 1
    for y in xs where r > 0:
        r = r + 1
    return r
