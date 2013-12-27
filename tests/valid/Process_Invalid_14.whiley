import println from whiley.lang.System

define Actor as ref { int data }

int Actor::get():
    return this->data

Actor ::createActor(int n):
    return new { data: n }

[Actor] ::createActors(int n):
    row = []
    for j in 0..n:
        m = createActor(j)
        row = row + [m]
    return row

void ::main(System.Console sys):
    actors = createActors(10)
    r = 0
    for i in 0..|actors|:
        r = r + actors[i].get()
    sys.out.println(Any.toString(r))
