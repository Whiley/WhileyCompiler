type Actor is {int data}

method get(&Actor this) -> int:
    return this->data

method createActor(int n) -> &Actor:
    return new {data: n}

method createActors(int n) -> ((&Actor)[] r)
requires n >= 0:
    (&Actor)[] row = [createActor(0); n]
    int j = 1
    while j < n where j >= 0 && |row| == n:
        &Actor m = createActor(j)
        row[j] = m
        j = j + 1
    return row

public export method test() :
    (&Actor)[] actors = createActors(10)
    int r = 0
    //
    int i = 0
    while i < |actors| where i >= 0:
        r = r + get(actors[i])
        i = i + 1
    //
    assume r == 45
