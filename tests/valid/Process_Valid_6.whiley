import println from whiley.lang.System

type Actor is &{int data}

method get(Actor this) => int:
    return this->data

method createActor(int n) => Actor:
    return new {data: n}

method createActors(int n) => [Actor]:
    row = []
    for j in 0 .. n:
        m = createActor(j)
        row = row + [m]
    return row

method main(System.Console sys) => void:
    actors = createActors(10)
    r = 0
    for i in 0 .. |actors|:
        r = r + actors[i].get()
    sys.out.println(Any.toString(r))
