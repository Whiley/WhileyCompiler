import whiley.lang.*
type Sized is { int value }
type SizeGetter is function(Sized) -> int
type SizeSetter is function(Sized,int) -> Sized

function f((SizeSetter|SizeGetter) x) -> int:
    if x is SizeGetter:
        return 1
    else:
        return 0

function getSize(Sized _this) -> int:
    return _this.value

function setSize(Sized _this, int value) -> Sized:
    return { value: value }

public export method test():    
    assume f(&getSize) == 1
    assume f(&setSize) == 0
