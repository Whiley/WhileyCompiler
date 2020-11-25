type Box is { Type elem }
type Borrow is { int n }
type Undefined is { Type base }

type Type is Box | Borrow | Undefined

public export method test():
    Type leaf = {n:0}
    Type box = { elem:leaf }
    Type undef = { base:leaf }
    assert box.elem == undef.base