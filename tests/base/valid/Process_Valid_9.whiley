define Actor as process { int data }

int Actor::get():
    return data

Actor System::createActor(int n):
    return spawn { data: n }

[Actor] System::createActors(int n):
    row = []
    for j in 0..n:
        m = this.createActor(n)
        row = row + m
    return row

void System::main([string] args):
    actors = this.createActors(10)
    r = 0
    for i in 0..|actors|:
        r = r + actors[i].get()
    out.println(str(r))