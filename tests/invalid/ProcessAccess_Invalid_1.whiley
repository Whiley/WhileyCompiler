type etype is {int mode, ...}

type Ptype is &etype

method get(Ptype _this) -> int:
    _this.op = 1
    return _this.mode
