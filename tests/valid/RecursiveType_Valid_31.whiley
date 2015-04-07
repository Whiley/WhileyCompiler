import whiley.lang.System

type Recursive is {Recursive}

function append(Recursive r1, Recursive r2) -> Recursive:
    if r1 == {}:
        return r2
    else:
        {Recursive} result = {}
        for r in r1:
            result = result + append(r,r2)
        return result

method main(System.Console console):
    Recursive r1 = {}
    Recursive r2 = append(r1,{{}})
    Recursive r3 = append(r2,{{}})
    console.out.println(r1)
    console.out.println(r2)
    console.out.println(r3)
