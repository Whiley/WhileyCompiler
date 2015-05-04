

type Recursive is {Recursive}

function append(Recursive r1, Recursive r2) -> Recursive:
    if r1 == {}:
        return r2
    else:
        {Recursive} result = {}
        for r in r1:
            result = result + append(r,r2)
        return result

public export method test():
    Recursive r1 = {}
    Recursive r2 = append(r1,{{}})
    Recursive r3 = append(r2,{{}})
    assume r1 == {}
    assume r2 == {{}}
    assume r3 == {{}}   
