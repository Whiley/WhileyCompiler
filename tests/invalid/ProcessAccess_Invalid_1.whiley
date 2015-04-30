type etype is {int mode, ...}

type Ptype is &etype

method get(Ptype this) -> int:
    this.op = 1
    return this.mode
