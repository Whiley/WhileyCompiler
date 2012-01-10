import * from whiley.lang.*

[string] f([string] args):
    return r

void ::main(System.Console sys,[string] args):
    l = [1,2,3]
    r = args + l
    f(r)
    sys.out.println(Any.toString(r))
