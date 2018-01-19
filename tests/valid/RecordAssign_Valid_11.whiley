type in_rec is {(int|null) f}
type ib_rec is {(int|bool) f}
type rec is in_rec | ib_rec

function read(rec x) -> (int|null|bool r):
    //
    if x is in_rec:
        return x.f
    else:
        return x.f

function write(rec x, int n) -> (rec r):
    //
    if x is in_rec:
        x.f = n
    else:
        x.f = n
    //
    return x

public export method test():
    //
    rec a = {f: null}
    assume read(a) == null
    //
    assume write(a,1) == {f:1}
