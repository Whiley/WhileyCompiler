

type Recursive is [Recursive]

function append(Recursive r1, Recursive r2) -> Recursive:
    if r1 == []:
        return r2
    else:
        r1[0] = append(r1[0],r2)
        return r1

public export method test():
    Recursive r1 = []
    Recursive r2 = append(r1,[[]])
    Recursive r3 = append(r2,[[]])
    assume r1 == []
    assume r2 == [[]]
    assume r3 == [[[]]]
