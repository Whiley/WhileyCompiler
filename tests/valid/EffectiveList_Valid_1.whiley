import println from whiley.lang.System

define rec as {int x, int y}

[bool|null] f([int] xs):
    r = []
    for x in xs:
        if x < 0:
            r = r + [true]
        else:
            r = r + [null]
    return r

void ::main(System.Console sys):
    e = []
    sys.out.println(f(e))
    e = [1,2,3,4]
    sys.out.println(f(e))
