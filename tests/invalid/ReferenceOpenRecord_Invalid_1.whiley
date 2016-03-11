type r is {int a, ...}
type r1 is {int a, int b, ...}
type r2 is {int a, bool b, ...}

public export method test():
    r1 x = (r1) {a:42, b:0}
    r2 y = (r2) {a:42, b:true}
    &r px = new(r) x
    &r py = new(r) y
    *px = *py
    r x1 = *px
    if x1 is r1:
        int z = x1.b
