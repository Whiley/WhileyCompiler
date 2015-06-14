type Actor is {int data}

method get(&Actor this) -> int:
    return this->data

method createActor(int n) -> &Actor:
    return new {data: n}

method createActors(int n) -> [&Actor]:
    [&Actor] row = []
    int j = 0
    while j < n:
        &Actor m = createActor(j)
        row = row ++ [m]
        j = j + 1
    return row

public export method test() -> void:
    [&Actor] actors = createActors(10)
    int r = 0
    //
    int i = 0
    while i < |actors|:
        r = r + get(actors[i])
        i = i + 1
    //
    assume r == 45
