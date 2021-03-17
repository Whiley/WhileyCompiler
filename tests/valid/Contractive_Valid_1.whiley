

type Contractive is Contractive[] | null

function f(Contractive x) -> (Contractive y):
    return x

public export method test() :
    Contractive x = f(null)
    assume x == null
