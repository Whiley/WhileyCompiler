import whiley.lang.*
type SizeGetter is function(Sized) -> int
type Sized is { SizeGetter getSize }

function f(null|SizeGetter x) -> int:
    if x is SizeGetter:
        return 1
    else:
        return 0

function getSize(Sized _this) -> int:
    return 1

public export method test():    
    assume f(&getSize) == 1
    assume f(null) == 0
