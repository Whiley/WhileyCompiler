
type nat is int where $ > 0

function f(int v) => int
ensures $ >= 0:
    i = 0
    while i < 100 where i >= 0:
        i = i - 1
        if i == v:
            break
        i = i + 2
    return i
