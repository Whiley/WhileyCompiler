

type etype is {int mode, ...}

type Ptype is &etype

method get(Ptype this) -> int:
    this->mode = 1
    return this->mode

public export method test() :
    assume get(new (etype) {mode:2}) == 1
    assume get(new (etype) {mode:3,x:1}) == 1
