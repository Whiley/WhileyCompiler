

type etype is {int mode, ...}

type Ptype is &etype

method get(Ptype _this) -> int:
    _this->mode = 1
    return _this->mode

public export method test():
    int result = get(new (etype) {mode:2})
    assume result == 1
    //
    result = get(new (etype) {mode:3,x:1})
    assume result == 1
