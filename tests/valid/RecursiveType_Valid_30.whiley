import whiley.lang.System

type Recursive is [Recursive]

function append(Recursive r1, Recursive r2) -> Recursive:
    if r1 == []:
        return r2
    else:
        r1[0] = append(r1[0],r2)
        return r1

method main(System.Console console):
    Recursive r1 = []
    Recursive r2 = append(r1,[[]])
    Recursive r3 = append(r2,[[]])
    console.out.println(r1)
    console.out.println(r2)
    console.out.println(r3)
