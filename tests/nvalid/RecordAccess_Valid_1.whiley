import println from whiley.lang.System

type etype is {int mode, ...}

type Ptype is ref etype

method get(Ptype this) => int:
    this->mode = 1
    return this->mode

method main(System.Console sys) => void:
    sys.out.println("OK")
