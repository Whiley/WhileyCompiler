

type etype is {int mode, ...}

type Ptype is &etype

method get(Ptype _this) -> int:
    _this->mode = 1
    return _this->mode

public export method test() :
    assume get(new (etype) {mode:2}) == 1
    assume get(new (etype) {mode:3,x:1}) == 1
