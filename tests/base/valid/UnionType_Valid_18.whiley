define TYPE as null|int

int f([TYPE] xs, TYPE p):
    r = 0
    for x in xs:
        if x == p:
            return r
        r = r + 1
    return -1

void System::main([string] args):
    this.out.println(str(f([null,1,2],null)))
    this.out.println(str(f([1,2,null,10],10)))
