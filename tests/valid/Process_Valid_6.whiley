

type Actor is {int data}

method get(&Actor this) -> int:
    return this->data

method createActor(int n) -> &Actor:
    return new {data: n}

method createActors(int n) -> [&Actor]:
    [&Actor] row = []
    for j in 0 .. n:
        &Actor m = createActor(j)
        row = row ++ [m]
    return row

public export method test() -> void:
    [&Actor] actors = createActors(10)
    int r = 0
    //
    for i in 0 .. |actors|:
        r = r + get(actors[i])
    //
    assume r == 45
