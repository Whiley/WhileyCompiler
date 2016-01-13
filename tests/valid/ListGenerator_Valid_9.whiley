function f() -> {bool b1, bool b2}[]:
    return [{b1: false,b2: false}; 2]

public export method test():
    {bool b1, bool b2} rec = {b1: false, b2: false}
    assume f() == [rec,rec]
