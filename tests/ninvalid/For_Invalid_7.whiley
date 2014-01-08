
type fr6nat is int where $ >= 0

function g({fr6nat} xs) => int
ensures $ > 1:
    r = 1
    for y in xs where r > 0:
        r = r + 1
    return r
