import * from whiley.lang.*

define Actor as process { int data }

int Actor::get():
    return this.data

Actor ::createActor(int n):
    return spawn { data: n }

[Actor] ::createActors(int n):
    row = []
    for j in 0..n:
        m = createActor(j)
        row = row + m
    return row

void ::main(System sys,[string] args):
    actors = createActors(10)
    r = 0
    for i in 0..|actors|:
        r = r + actors[i].get()
    sys.out.println(toString(r))
